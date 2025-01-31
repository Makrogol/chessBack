#pragma once

#include "main.h"

class MainFactory {
public:
    static Main* getOrCreateMain();

    static void destroyMain();

private:
    static Main* main;
};

