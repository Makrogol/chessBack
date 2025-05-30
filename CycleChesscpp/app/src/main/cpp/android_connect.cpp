#include <jni.h>

#include "main_factory.h"


// TODO сделать возможность получать фул доску, чтобы можно было ее отрисовывать без доработок на клиенте
// TODO сделать возможность получать - есть ли фигура на такой-то позиции

extern "C" JNIEXPORT void JNICALL
Java_com_serebryakov_cyclechesscpp_application_model_cppapi_CppConnectionKt_startGameCpp(JNIEnv* env, jobject /* this */, jstring jmainColorString) {
    const std::string mainColorString = env->GetStringUTFChars(jmainColorString, JNI_FALSE);
    MainFactory::getOrCreateMain()->startGame(mainColorString);
}

extern "C" JNIEXPORT void JNICALL
Java_com_serebryakov_cyclechesscpp_application_model_cppapi_CppConnectionKt_startGameWithFenCpp(JNIEnv* env, jobject /* this */, jstring jmainColorString, jstring jfenString) {
    const std::string mainColorString = env->GetStringUTFChars(jmainColorString, JNI_FALSE);
    const std::string fenString = env->GetStringUTFChars(jfenString, JNI_FALSE);
    MainFactory::getOrCreateMain()->startGameWithFen(mainColorString, fenString);
}

extern "C" JNIEXPORT void JNICALL
Java_com_serebryakov_cyclechesscpp_application_model_cppapi_CppConnectionKt_startGameWithReversedFenCpp(JNIEnv* env, jobject /* this */, jstring jmainColorString, jstring jfenString) {
    const std::string mainColorString = env->GetStringUTFChars(jmainColorString, JNI_FALSE);
    const std::string fenString = env->GetStringUTFChars(jfenString, JNI_FALSE);
    MainFactory::getOrCreateMain()->startGameWithReversedFen(mainColorString, fenString);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_serebryakov_cyclechesscpp_application_model_cppapi_CppConnectionKt_getFenCpp(JNIEnv* env, jobject /* this */) {
    return env->NewStringUTF(MainFactory::getOrCreateMain()->getFen().c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_serebryakov_cyclechesscpp_application_model_cppapi_CppConnectionKt_getCurrentTurnColorCpp(JNIEnv* env, jobject /* this */) {
    return env->NewStringUTF(MainFactory::getOrCreateMain()->getCurrentTurn().c_str());
}

extern "C" JNIEXPORT void JNICALL
Java_com_serebryakov_cyclechesscpp_application_model_cppapi_CppConnectionKt_endGameCpp(JNIEnv* env, jobject /* this */) {
    MainFactory::destroyMain();
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_serebryakov_cyclechesscpp_application_model_cppapi_CppConnectionKt_getPossibleMovesForPositionCpp(JNIEnv* env, jobject /* this */, jstring jpositionString) {
    const std::string positionString = env->GetStringUTFChars(jpositionString, JNI_FALSE);
    return env->NewStringUTF(MainFactory::getOrCreateMain()->getPossibleMovesForPosition(positionString).c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_serebryakov_cyclechesscpp_application_model_cppapi_CppConnectionKt_tryDoMoveCpp(JNIEnv* env, jobject /* this */, jstring jpositionsString) {
    const std::string positionsString = env->GetStringUTFChars(jpositionsString, JNI_FALSE);
    return env->NewStringUTF(MainFactory::getOrCreateMain()->tryDoMove(positionsString).c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_serebryakov_cyclechesscpp_application_model_cppapi_CppConnectionKt_tryDoMoveV2Cpp(JNIEnv* env, jobject /* this */, jstring jmoveString) {
    const std::string moveString = env->GetStringUTFChars(jmoveString, JNI_FALSE);
    return env->NewStringUTF(MainFactory::getOrCreateMain()->tryDoMoveV2(moveString).c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_serebryakov_cyclechesscpp_application_model_cppapi_CppConnectionKt_getGameStateCpp(JNIEnv* env, jobject /* this */) {
    return env->NewStringUTF(MainFactory::getOrCreateMain()->getGameState().c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_serebryakov_cyclechesscpp_application_model_cppapi_CppConnectionKt_tryDoMagicPawnTransformationCpp(JNIEnv* env, jobject /* this */, jstring jpositionAndPieceTypeString) {
    const std::string positionAndPieceTypeString = env->GetStringUTFChars(jpositionAndPieceTypeString, JNI_FALSE);
    return env->NewStringUTF(MainFactory::getOrCreateMain()->tryDoMagicPawnTransformation(positionAndPieceTypeString).c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_serebryakov_cyclechesscpp_application_model_cppapi_CppConnectionKt_getKingPositionByColorCpp(JNIEnv* env, jobject /* this */, jstring jcolorString) {
    const std::string colorString = env->GetStringUTFChars(jcolorString, JNI_FALSE);
    return env->NewStringUTF(MainFactory::getOrCreateMain()->getKingPositionByColor(colorString).c_str());
}
