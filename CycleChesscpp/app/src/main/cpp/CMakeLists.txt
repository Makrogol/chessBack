cmake_minimum_required(VERSION 3.22.1)

project(cyclechesscpp)

set (CMAKE_CXX_STANDARD 20)

set(src
        board/board.cpp
        board/cell.cpp

        pieces/utils/color.cpp
        pieces/utils/offset.cpp
        pieces/utils/piece_type.cpp
        pieces/utils/position.cpp

        pieces/bishop.cpp
        pieces/king.cpp
        pieces/knight.cpp
        pieces/pawn.cpp
        pieces/queen.cpp
        pieces/rook.cpp

        game/game.cpp
        game/history_record_manager.cpp
        game/history_record.cpp
        game/history.cpp

        main/main_factory.cpp
        main/main.cpp
        main/parser.cpp
        main/unparser.cpp
)

if (PYTHON)
set(src ${src}
        python_connect.cpp
)
endif()


if (ANDROID)
set(src ${src}
        android_connect.cpp
)
endif()


if (NOT GTEST)
add_library(${CMAKE_PROJECT_NAME} SHARED ${src})

target_include_directories(${CMAKE_PROJECT_NAME} PUBLIC
        ${CMAKE_CURRENT_SOURCE_DIR}/board
        ${CMAKE_CURRENT_SOURCE_DIR}/pieces
        ${CMAKE_CURRENT_SOURCE_DIR}/game
        ${CMAKE_CURRENT_SOURCE_DIR}/main
)
endif()



if (ANDROID)
target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log)
endif()



if (GTEST)
set(src ${src}
        ut/main.cpp
        ut/test_board.cpp
        ut/test_pieces.cpp
)
add_executable(test ${src})

target_link_libraries(test
        gtest
        gmock
        pthread
)

target_include_directories(test PUBLIC
        ${CMAKE_CURRENT_SOURCE_DIR}/board
        ${CMAKE_CURRENT_SOURCE_DIR}/pieces
        ${CMAKE_CURRENT_SOURCE_DIR}/game
        ${CMAKE_CURRENT_SOURCE_DIR}/main
        ${CMAKE_CURRENT_SOURCE_DIR}/ut
)

endif()



