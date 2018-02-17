# MovieKu
[![API](https://img.shields.io/badge/API-21%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=21)

![Logo](https://res.cloudinary.com/hyuwah-github-io/image/upload/c_scale,w_128/v1518845019/movieku-logo.png)

Movie information android app. An Indonesia Android Kejar Intermediate class final project. Data provided by The Movie Database (TMDb) API.

**Features**

* 5 Category (Now playing, Popular, Top rated, Latest & Upcoming)
* Search movies
* See Trailer & Review _(if the movie has one)_
* Share movie info

## Screenshot

![Home](http://res.cloudinary.com/hyuwah-github-io/image/upload/v1518844392/movieku-0.5-home.png)
![Home Drawer](https://res.cloudinary.com/hyuwah-github-io/image/upload/v1518843832/movieku-0.5-home-drawer.png)
![Home Search](https://res.cloudinary.com/hyuwah-github-io/image/upload/v1518844879/movieku-0.5-home-search.png)
![Detail](http://res.cloudinary.com/hyuwah-github-io/image/upload/v1518843986/movieku-0.5-detail.png)


## Download

Unsigned APK : [MovieKu v0.5](https://drive.google.com/file/d/1lZNSJm3s3WpwOPQ4DFRHxupS0NkgM-Em/view?usp=sharing)

Playstore coming soon :)

## Getting Started

* Clone project : `https://github.com/hyuwah/movieku.git`
* Sync gradle
* Daftar akun TMDb
    * Request API Key di https://www.themoviedb.org/settings/api
* Ganti API_KEY di file `TheMovieDbService.java` dengan API_KEY v3 yang sudah diperoleh
* Rebuild project, run

## Info

Target SDK : 26

### Library Used

```gradle
// Buat RecyclerView, CardView & Nav Drawer
compile 'com.android.support:design:26.1.0'
implementation 'com.android.support:cardview-v7:26.1.0'
implementation 'com.android.support:support-v4:26.1.0'

// Retrofit
compile 'com.squareup.retrofit2:retrofit:2.3.0'

// GSON converter
compile 'com.squareup.retrofit2:converter-gson:2.3.0'

// Logging Interceptor
compile 'com.squareup.okhttp3:logging-interceptor:3.8.0'

// Picasso
compile 'com.squareup.picasso:picasso:2.5.2'

// Butterknife
compile 'com.jakewharton:butterknife:8.8.1'
annotationProcessor 'com.jakewharton:butterknife-compiler:8.8.1'
```


## License
MIT