#include "main_factory.h"

Main* MainFactory::getOrCreateMain() {
    if (main == nullptr) {
        main = new Main();
    }
    return main;
}

void MainFactory::destroyMain() {
    delete main;
    main = nullptr;
}

Main* MainFactory::main = nullptr;
