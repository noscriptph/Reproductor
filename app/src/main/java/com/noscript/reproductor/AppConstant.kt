package com.noscript.reproductor



data class Song(
    val numero: Int,
    val title:String,
    val audioResId:Int,
    val imageResId: Int
){
}

class AppConstant{
    companion object{
        const val LOG_MAIN_ACTIVITY="MainActivityReproductor"
        const val MEDIA_PLAYER_POSITION="noPosition"

        val songs=listOf(
            Song(1,"Noisecontrollers & Wildstylez People are awesome", R.raw.peopleareawesome,R.drawable.cover_foreground),
            Song(2,"Headhunterz ft. Malukah - Reignite D-Block & S-te-Fan remix", R.raw.headhunterz,R.drawable.headhunterz),
            Song(3,"TNT - Countdown (Radical Redemption Remix)", R.raw.countdowntnt, R.drawable.countdown)
        )
    }
}