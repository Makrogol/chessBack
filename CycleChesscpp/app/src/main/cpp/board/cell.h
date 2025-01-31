#pragma once
#include <memory>

#include "utils/color.h"
#include "utils/position.h"

class Piece;

class Cell {
public:
    using PPiece = std::shared_ptr<Piece>;

    Cell() = default;

    Cell(Color color);

    Cell(Position position, Color color);

    Cell(Position position, Color color, PPiece ppiece);

    void changePieceTo(PPiece ppiece);

    Color getColor() const {
        return color;
    }

    PPiece getPiece() const {
        return piece;
    }

    bool hasPiece() const {
        return piece != nullptr;
    }

    Position getPosition() const {
        return position;
    }

private:
    PPiece piece = nullptr;
    const Position position;
    Color color = Color::NO_COLOR;
};
