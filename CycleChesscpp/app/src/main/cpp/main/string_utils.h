#pragma once
#include <string>
#include <vector>

// TODO перенести в отдельную папку с утилями

using String = std::string;
using Strings = std::vector<String>;


Strings split(const String& splitString, const String& splitCharacter);
String join(const String& joinCharacter, const Strings& joinStrings);
int stringToInt(const String& string);
