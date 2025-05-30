package com.serebryakov.cyclechesscpp.application

import android.app.Application
import com.serebryakov.cyclechesscpp.application.model.back.Api
import com.serebryakov.cyclechesscpp.application.model.back.socket.WebSocketHolderImpl
import com.serebryakov.cyclechesscpp.application.model.back.socket.listener.WebSocketListener
import com.serebryakov.cyclechesscpp.application.model.cppapi.CppConnectionApiImpl
import com.serebryakov.cyclechesscpp.application.model.sharedpref.jwttoken.JwtTokenSharedPref
import com.serebryakov.cyclechesscpp.application.model.sharedpref.jwttoken.JwtTokenSharedPrefImpl
import com.serebryakov.cyclechesscpp.application.model.sharedpref.localgamefen.LocalGameFenSharedPref
import com.serebryakov.cyclechesscpp.application.model.sharedpref.localgamefen.LocalGameFenSharedPrefImpl
import com.serebryakov.cyclechesscpp.application.model.sharedpref.username.UsernameSharedPref
import com.serebryakov.cyclechesscpp.application.model.sharedpref.username.UsernameSharedPrefImpl
import com.serebryakov.cyclechesscpp.application.repository.backrepository.BackRepositoryImpl
import com.serebryakov.cyclechesscpp.application.repository.cppconnectionrepository.CppConnectionRepositoryImpl
import com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.jwttoken.JwtTokenSharedPrefRepository
import com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.jwttoken.JwtTokenSharedPrefRepositoryImpl
import com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.localgamefen.LocalGameFenSharedPrefRepository
import com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.localgamefen.LocalGameFenSharedPrefRepositoryImpl
import com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.username.UsernameSharedPrefRepository
import com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.username.UsernameSharedPrefRepositoryImpl
import com.serebryakov.cyclechesscpp.application.repository.socketrepository.SocketRepositoryImpl
import com.serebryakov.cyclechesscpp.foundation.BaseApplication
import com.serebryakov.cyclechesscpp.foundation.model.IoDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


//Singltone Scope
class App : Application(), BaseApplication {

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL_BASE)
        .addConverterFactory(GsonConverterFactory.create()).build()
    private val productApi = retrofit.create(Api::class.java)

    private val ioDispatcher = IoDispatcher(Dispatchers.IO)
    private val backRepository = BackRepositoryImpl(productApi, ioDispatcher)

    private val webSocketListener = WebSocketListener()

    private val socketHolder = WebSocketHolderImpl(webSocketListener = webSocketListener)
    private val socketRepository = SocketRepositoryImpl(socketHolder, ioDispatcher)

    private val cppApi = CppConnectionApiImpl()
    private val cppConnectionRepository = CppConnectionRepositoryImpl(cppApi)

    private lateinit var jwtTokenSharedPref: JwtTokenSharedPref
    private lateinit var jwtTokenSharedPrefRepository: JwtTokenSharedPrefRepository

    private lateinit var usernameSharedPref: UsernameSharedPref
    private lateinit var usernameSharedPrefRepository: UsernameSharedPrefRepository

    private lateinit var localGameFenSharedPref: LocalGameFenSharedPref
    private lateinit var localGameFenSharedPrefRepository: LocalGameFenSharedPrefRepository


    override fun onCreate() {
        super.onCreate()
        jwtTokenSharedPref = JwtTokenSharedPrefImpl(applicationContext)
        jwtTokenSharedPrefRepository =
            JwtTokenSharedPrefRepositoryImpl(jwtTokenSharedPref, ioDispatcher)

        usernameSharedPref = UsernameSharedPrefImpl(applicationContext)
        usernameSharedPrefRepository =
            UsernameSharedPrefRepositoryImpl(usernameSharedPref, ioDispatcher)

        localGameFenSharedPref = LocalGameFenSharedPrefImpl(applicationContext)
        localGameFenSharedPrefRepository =
            LocalGameFenSharedPrefRepositoryImpl(localGameFenSharedPref, ioDispatcher)

        singletonScopeDependencies.add(jwtTokenSharedPrefRepository)
        singletonScopeDependencies.add(usernameSharedPrefRepository)
        singletonScopeDependencies.add(localGameFenSharedPrefRepository)
    }

    override suspend fun closeSocket() {
        socketRepository.closeSocket()
    }

    override val singletonScopeDependencies = mutableListOf<Any>(
        backRepository,
        socketRepository,
        cppConnectionRepository,
        webSocketListener,
    )

    companion object {
        const val URL_BASE = "http://130.193.53.45:80"
    }
}
