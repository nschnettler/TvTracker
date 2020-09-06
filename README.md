# TVTracker (WIP, name not final)

TVTracker is a wip tv show browsing app. It uses the [Trakt](https://trakt.tv/) API to deliver show toplists, the 
[The Movie Database](https://www.themoviedb.org/) API for additional images and detailed information and the [The TVDB](https://thetvdb.com/) API for 
Actor information and pictures.

## Development Setup
The project should work with any recent version of Android Studio.

### API Keys
The app uses various APIs to deliver dynamic data. You need to supply your own API keys for [Trakt.tv](https://trakt.tv/oauth/applications/new), 
[TMDB](https://www.themoviedb.org/settings/api) and [TheTVDB](https://thetvdb.com/api-information).

After you obtained the api keys you can provide them to the app by putting the following in the
`gradle.properties` file in your user home:

```
# Get these from Trakt.tv
traktClientId = <insert>
traktClientSecret = <insert>

# Get this from TMDb
tmdbKey = <insert>

# Get this from TVDB
tvdbKey = <insert>
```

Linux/Mac: `~/.gradle/gradle.properties`
Windows: `C:\Users\USERNAME\.gradle`
