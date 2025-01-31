 #pragma once

class Offset {
public:
    Offset();
    Offset(int i, int j);

    int getI() const {
        return i;
    }

    int getJ() const {
        return j;
    }


private:
    int i = 0;
    int j = 0;
};
