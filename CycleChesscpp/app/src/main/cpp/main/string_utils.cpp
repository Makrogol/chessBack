#include "string_utils.h"

String join(const String& joinCharacter, const Strings& joinStrings) {
    String result;
    for (const String& joinString : joinStrings) {
        result += joinString + joinCharacter;
    }
    const int substrN = result.size() - joinCharacter.size();
    return result.substr(0, substrN);
}

Strings split(const String& splitString, const String& splitCharacter) {
    Strings splitStrings;
    int start, end = -1 * splitCharacter.size();
    do {
        start = end + splitCharacter.size();
        end = splitString.find(splitCharacter, start);
        splitStrings.push_back(splitString.substr(start, end - start));
    } while (end != -1);
    return splitStrings;
}

int stringToInt(const String& string) {
    return std::atoi(string.c_str());
}
