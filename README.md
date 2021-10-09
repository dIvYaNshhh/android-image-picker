# Android Image Picker
![Jit](https://img.shields.io/jitpack/v/github/dIvYaNshhh/android-image-picker?style=for-the-badge&color=2F9319) 


:sparkles: A simple image picker library which supported all android versions


## Setup

Add it in your root `build.gradle` at the end of repositories:

```groovy
allprojects {
    repositories {
        //...omitted for brevity
        maven { url 'https://jitpack.io' }
    }
}
```



Add the dependency

```groovy
dependencies {
   implementation "com.github.dIvYaNshhh:android-image-picker:$latest_release"
}
```

## Usage
Sample implementation [here](app/)

### Initialise

- Initialise and register listener `ImagePicker` in your Activity


```kotlin
    class MainActivity : AppCompatActivity(), ImagePicker.OnImageSelectedListener {

    lateinit var imagePicker: ImagePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imagePicker = ImagePicker(this, BuildConfig.APPLICATION_ID)
        imagePicker.setImageSelectedListener(this)
    }
```

### Take photo from camera

- Add `From Camera` in your layout xml file

```kotlin
    imagePicker.takePhotoFromCamera()
```

### Take photo from gallery

- Add `From Gallery` in your layout xml file

```kotlin
    imagePicker.takePhotoFromGallery()
```

### Callback ImagePicker.OnImageSelectedListener

- Add `OnImageSelectedListener` in your Activity

```kotlin
    imagePicker.setImageSelectedListener(this)

```

- Implement `Callback methods` in your Activity

```kotlin
    fun onImageSelectSuccess(imagePath: String)
    fun onImageSelectFailure()
```



### Licensed under the [MIT License](LICENSE)

```
MIT License

Copyright (c) 2021 Divyansh Ingle

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
