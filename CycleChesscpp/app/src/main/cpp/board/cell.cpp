#include "cell.h"

// #include <android/log.h>

Cell::Cell(Color color): color(color) {}

Cell::Cell(Position position, Color color): position(position), color(color) {}

Cell::Cell(Position position, Color color, Cell::PPiece ppiece): position(position), color(color) {
    changePieceTo(ppiece);
}

void Cell::changePieceTo(Cell::PPiece ppiece) {
    piece.reset();
    piece = ppiece;
}
