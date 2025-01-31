#include "color.h"

Color getAnotherColor(const Color color) {
    switch (color)
    {
    case Color::WHITE:
        return Color::BLACK;
    case Color::BLACK:
        return Color::WHITE;
    default:
        // error
        return Color::NO_COLOR;
    }
}

Colors getAllColors() {
    return {
        Color::WHITE,
        Color::BLACK
    };
}

std::string getTurnColorFen(const Color color) {
    switch (color)
    {
    case Color::WHITE:
        return "w";
    case Color::BLACK:
        return "b";
    default:
        // TODO error need log
        return " ";
    }
}
