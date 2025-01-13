<h1 align="center">Welcome to Vanish Composable Library 👋</h1>

<p align="center">
  <a href="https://github.com/frinyvonnick/gitmoji-changelog">
    <img src="https://img.shields.io/badge/API-15%2B-blue.svg?style=flat" alt="gitmoji-changelog">
  </a>  <a href="https://github.com/frinyvonnick/gitmoji-changelog">
    <img src="https://jitpack.io/v/mejdi14/AndroidColorPicker.svg" alt="gitmoji-changelog">
  </a>
  </a>
	<a href="https://github.com/kefranabg/readme-md-generator/blob/master/LICENSE">
    <img alt="License: MIT" src="https://img.shields.io/badge/license-MIT-yellow.svg" target="_blank" />
  </a>
  <a href="https://codecov.io/gh/kefranabg/readme-md-generator">
    <img src="https://codecov.io/gh/kefranabg/readme-md-generator/branch/master/graph/badge.svg" />
  </a>
</p>

## ✨ Demo

<p align="center">
  <img 
    src="https://raw.githubusercontent.com/mejdi14/Vanish-Composable/main/demo/output.gif" 
    height="700" 
    width="550"
  />
</p>


## :art:Design inspiration

many thanks goes to [A. Hassan](https://x.com/azhassan_) for the beautiful design and
animation

## Installation

Add this to your module's `build.gradle` file (make sure the version matches the JitPack badge
above):

```gradle
dependencies {
	...
	implementation("io.github.mejdi14:vanish_composable:0.0.1")
}
```

## :fire:How to use

``` java
                            var controller: AnimationController? by remember { mutableStateOf(null) }
                            VanishComposable(
                                Modifier,
                                VanishOptions = VanishOptions(),
                                effect = AnimationEffect.SHATTER,
                                onControllerReady = {
                                    controller = it
                                }
                            ) {
                                // Your Composable
                                ContentComposable()
                            }
```

Animation Listener
-----

``` java
// Start animation
controller?.triggerVanish() {
                        // Do something when animation finishes
                    }
                    
// Reverse animation                    
controller?.reset()
```

Animation types
-----

``` java
    PIXELATE,
    SWIRL,
    SCATTER,
    SHATTER,
    WAVE,
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT,
    UP,
    DOWN,
    DISSOLVE,
    EXPLODE
```

Hold animation duration after separation
-----

``` java
 .timeBetweenAnimations
```

Configuration options
-----

``` java
  pixelSize: size of each pixel or dot 
  pixelSpacing: space between pixels when they are separated
  pixelVelocity: velocity of the pixels
  animationDuration: duration of the animation from start to finish
  triggerFinishAt: use this if you want to trigger the end of animation a bit earlier (1f: wait to end, 0f: don't wait)
```



## 🤝 Contributing

Contributions, issues and feature requests are welcome.<br />
Feel free to check [issues page] if you want to contribute.<br />

## Author

👤 **Mejdi Hafiane**

- profile: [@MejdiHafiane](https://twitter.com/mejdi141)

## Show your support

Please ⭐️ this repository if this project helped you!

## 📝 License

Copyright © 2019 [Mejdi Hafiane](https://github.com/mejdi14).<br />
This project is [MIT](https://github.com/mejdi14/readme-md-generator/blob/master/LICENSE) licensed.
