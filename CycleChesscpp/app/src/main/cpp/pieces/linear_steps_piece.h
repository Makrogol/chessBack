#pragma once
#include "equal_steps_piece.h"


// #include <android/log.h>
#include <vector>


class LinearStepsPiece : public EqualStepsPiece {
public:
    using Positions = std::vector<Position>;
    using Line = std::vector<Offset>;
    using Lines = std::vector<Line>;

    LinearStepsPiece(const Position& position, const Color& color, const PieceType& type) :
            EqualStepsPiece(position, color, type) {}

protected:
    // Считаем, что порядок у линий и линий за границей одинаковый
    // То есть, что первая линия за границей является продолжением первой просто линии
    // И соответственно, что размеры у них одинаковые
    virtual Lines getLines() const = 0;
    virtual Lines getLinesOverBoard() const = 0;

    // Создаем линию (пока не дойдем до края доски)
    // На вход принимаются -1, 0, +1 как направления движения
    // Отсчитывать начинаем от переданной
    Line createLine(int i, int j, const Position& position, const bool isLineOverBoard = false) const {
        Line line;
        for (int k = !isLineOverBoard; k < 8; ++k) {
            Offset step(i * k, j * k);
            if (isStepOverBoard(position, step)) {
                break;
            }
            if (isLineOverBoard) {
                line.push_back(getOffsetBetweenPositions(MakeOffset(position, step), this->position));
            } else {
                line.push_back(step);
            }
        }
        return line;
    }
    
    // Создаем линию (пока не дойдем до края доски)
    // На вход принимаются -1, 0, +1 как направления движения
    // Отсчитывать начинаем от текущей позиции
    Line createLine(int i, int j) const {
        return createLine(i, j, position);
    }

    // Создаем линию как продолжение линии за краем доски
    // То есть для ладьи это будет по той же самой вертикали/горизонтали
    // Но считая от начала поля
    // На вход принимаются -1, 0, +1 как направления движения
    Line createLineOverBoard(int i, int j) const {
        // Чтобы учитывать и самую первую клетку (так как в createLine мы начинаем двигаться с 1 сразу же)
        // Надо здесь брать не начало линии, а на 1 ниже, так сказать начинать из-за края доски
        // А точнее вместо этого просто будем передавать булевый флаг - прям прямой и явный костыль
        // Который будет означать, что надо начинать с 0, а не с 1
        // TODO подумать, как это можно сделать по лучше без костыля
        return createLine(i, j, getStartOfLine(i, j), true);
    }

    // Принимаем на вход соторну, в которую двигаемся
    // Задается двумя координатами
    // Например, если придет -1, -1 это значит, двигаемся
    // влево вверх и соответственно начало линии (диагонали) будет лежать справа внизу
    // Соответственно принимаем -1, 0, +1
    // Если придет 0, -1 это значит двигаемся влево, значит начало линии (горизонтали)
    // будет лежать справа
    Position getStartOfLine(int i, int j) const {
        if (!isMoveCorrect(i) || !isMoveCorrect(j)) {
            return Position();
        }

        for (int k = 1; k < 8; ++k) {
            Offset step(-i * k, -j * k);
            // TODO костыль, который нужен был, чтобы если фигура стоит на краю доски мы могли считать для нее
            // Ходы за краем (если она может)
            Offset prevStep(-i * (k - 1), -j * (k - 1));
            if (isStepOverBoard(position, step)) {
                return MakeOffset(position, prevStep);
            }

            if (isStepInBorderByMove(position, step, i, j)) {
                return MakeOffset(position, step);
            }
        }

        // Сюда мы доходить не должны, так как должны дойти до края доски за 8 итераций
        return Position();
    }

    // TODO фигня, надо переделать, конкретно - вызов этой функции
    // И то, что она виртуальная, как будто это можно как-то по лучше организовать
    virtual Positions getStartsOfAllLines() const = 0;

private:
    Offsets getSteps(const Board& board) const final {
        Offsets offsets;
        const Lines lines = getLines();
        const Lines linesOverBoard = getLinesOverBoard();
        bool isBadLine = false;
        const Positions startOfLines = getStartsOfAllLines();

        if (lines.size() != linesOverBoard.size()) {
            return offsets;
        }

        for (int i = 0; i < lines.size(); ++i) {
            isBadLine = false;
            for (const Offset& step : lines[i]) {
                if (board.hasPieceSameColor(position, step, color)) {
                    isBadLine = true;
                    break;
                }
                if (board.hasPieceAnotherColor(position, step, color)) {
                    // safeInsertOffset - на всякий случай, тут не должно быть выходов за границу
                    safeInsertOffset(offsets, step);
                    isBadLine = true;
                    break;
                }
                // safeInsertOffset - на всякий случай, тут не должно быть выходов за границу
                safeInsertOffset(offsets, step);
            }

            if (isBadLine || !canDoStepOverBoard()) {
                continue;
            }

            for (const Offset& step : linesOverBoard[i]) {
                if (board.hasPieceSameColor(position, step, color)) {
                    break;
                }
                if (board.hasPieceAnotherColor(position, step, color)) {
                    // safeInsertOffset - на всякий случай, тут не должно быть выходов за границу
                    safeInsertOffset(offsets, position, step);
                    break;
                }
                // safeInsertOffset - на всякий случай, тут не должно быть выходов за границу
                safeInsertOffset(offsets, position, step);
            }
        }

        return offsets;
    }

    bool isMoveCorrect(int i) const {
        return (i == -1) || (i == 0) || (i == 1);
    }
};
