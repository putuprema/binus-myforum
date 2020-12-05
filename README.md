# BINUS MyForum

![BINUS MyForum](https://i.imgur.com/4JUYdGi.png)

BINUS MyForum is an android application that gives easy access for Binusian (college students of BINUS University) to unreplied forum discussions. In BINUS, we have mandatory 
forum discussions that we need to reply by answering the lectures questions within a certain period of time (usually in 1 week). In my current university's mobile application,
it is very unintuitive to access these forums. We need to go to very deep menus with very slow response time due to a lot of unnecessary network requests. So I create this
application to solve just that, by displaying list of forum threads on the home page. Binusian can reply to the forums directly in this application, and they will also get
reminded to reply to their forums everyday.

Your data is saved only on your device storage. The only network requests this app does are to BINUSMAYA API for login and fetching forum threads list and
to Firebase for performance and crash analytics.

## Language, libraries, and tools
- [Kotlin](https://kotlinlang.org/)
- [Room](https://developer.android.com/topic/libraries/architecture/room.html), for storing student details and forum thread list cache
- [Dagger 2 + Hilt](https://developer.android.com/training/dependency-injection/hilt-android), for dependency injection (DI)
- [Retrofit](http://square.github.io/retrofit/), for handling API requests
- [OkHttp](http://square.github.io/okhttp/)
- [Gson](https://github.com/google/gson)
- [Firebase](https://firebase.google.com/), for performance and crash analytics
- [Apache Commons Codec](https://commons.apache.org/proper/commons-codec/), for dealing with hashing, encoding, and encryption
- [Apache Commons IO](https://commons.apache.org/proper/commons-io/), for dealing with files (ie. getting human readable file size, etc)
- [Apache Commons Lang3](https://commons.apache.org/proper/commons-lang/), for dealing with string validation

## Architecture
I try to follow MVI (Model-View-Intent) architecture for this project. The View sends commands to the ViewModel through intents (or events) to get the data it needs. The ViewModel grabs that
data from Repository in model layer (either from local database or from BINUSMAYA api for the updated version), then saves it to a LiveData that the View can observe to 
display it once available for user to see. 

![MVI Architecture](https://miro.medium.com/max/700/1*ohFythvIKvgVUy_08dF4Ag.png)

I really liked the architecture the first time I learnt it, because it has clear separation of concern and gives me clear flow of data.
I'm still learning a lot about android development so there may be mistakes on how I implement the architecture. Any feedback would be very appreciated.

## Contributing
Feel free to fork this repository and contribute to fix bugs or even bring other BINUSMAYA / BINUS Mobile features to this application. If you are new to open source,
here's some steps to get started:
1. Create a new issue, explaining the problem you want to solve or features you want to add, the purpose, and your approach in solving the problem. That way, we can have
nice discussion regarding the best approach to achieve what you want.
2. Fork this repository and push your changes there.
3. When you are ready, create pull request to this repository's main branch.
4. We will review the changes together to see if there is potential issues or improvements to your implementation.
5. If everything is good, I will merge your pull request and publish the update to users.

The best way to learn any software development for me is to actually create a real-world project. So I hope a lot of Binusian interested in learning about android development
can contribute to this project. It would be very awesome if this application has more features than just forum. Unfortunately, I don't have much time to implement other features.
That's why I need you, Binusian, to further develop this project. 
