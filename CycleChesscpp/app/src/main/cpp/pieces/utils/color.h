#pragma once

#include <string>
#include <vector>

enum Color {
    NO_COLOR,
    WHITE,
    BLACK,
};

using Colors = std::vector<Color>;

Color getAnotherColor(const Color color);
Colors getAllColors();

// Раскидать то, что относится к фену в один файл
std::string getTurnColorFen(const Color color);
