This project contains several useful classes for Android developers.
While writing this, I tried to follow KISS principle.

So, there's small overview about existing utils:

utils
 |- StringUtils             - some useful methods for Strings
 |- Locations               - a little bit simplified way to recieve current user's location
 |- UserPreferences         - easy way to use default Android's SharedPreferences
 |- ObbExpansionsManager    - if your APK is more than 50 MB you definitely will need some class like this
 |
 |- view                    - this package contains several activities with some
 |  |                         basic functionality
 |  |- ActivityWithAds      - add Ads to your app and start to make money
 |  |- ActivityWithMoreApps - this activity will allow you to show "More Apps" screen
 |  |                         (I'm using Chartboost service, it's really simple and useful)
 |  '- LicensedActivity     - use this class if you want to create paid application
 |                            with licensing control
 |                            (see also: SimplePolicy)
 |
 |- licensing
 |  '- SimplePolicy         - simplified version of ServerManagedPolicy.
 |                            DON'T FORGET TO CHANGE SALT!
 |
 '- social                  - this is package with several utilities for social networks.
    |                         And e-mail.
    |- EmailSender          - send e-mail with specified recepient(s), subject and message.
    |                         You can also send file via e-mail.
    |- FacebookSender       - with this class you can share images and/or messages
    |                         to your wall in the name of your app
    |- TwitterSender        - same as FacebookSender.
    '- SenderCallback       - UI callbacks for TwitterSender and FacebookSender

See descriptions of each class for further information.
