# ImproperUI
The ultimate solution to Minecraft Rendering being too hard to learn.

---

### Introduction
Unlike my other project, ClickCrystals Script, ImproperUI Script requires you to have prior knowledge of 

- Basic CSS properties and how they work
- Basic HTML structures
- Java

ImproperUI's syntax is highly inspired by CSS (Cascading Style Sheets), thought it is not entirely identical.

Below is a screenshot of an interactive screen with draggable and scrollable elements as the home page of the mod.
This screen is scripted using ImproperUI Script:

![demo](./assets/demo.png)

### Adding ImproperUI to your Project
To add ImproperUI, you download it as a jar and then add it to gradle manually.
I didn't want to create an online repository and I didn't want to make it a separate mod. Womp Womp.

#### Step 1
Eliminate possible duplicates in case other projects also use ImproperUI.
```gradle
jar {
    from("LICENSE") {
        rename { "${it}_${project.archivesBaseName}"}
    }
    from {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        configurations.runtimeClasspath.collect {
            it.isDirectory() ? it : zipTree(it)
        }
    }
}
```

#### Step 2
[Download the jar](https://github.com/ItziSpyder/ImproperUI/releases/latest) and add it to your project files, as well as build.gradle as a dependency.
```gradle
dependencies {
    compileOnly files("libs/ImproperUI-theVersionYouWant.jar")
}
```

#### Step 3
Add the downloaded jar, as a mod, to your mods folder! You have to this use this a library!

#### Step 4 (Final Step)
Initialize the API. In this section, you call the init() function on ImproperUIAPI, then
provide:
- the `modId` your YOUR mod
- the `main class mod initializer` of YOUR mod
- then finally a list of script paths inside the `src/main/resources/` folder of YOUR mod.

```java
public class ImproperUI implements ModInitializer {

    @Override
    public void onInitialize() {
        ImproperUIAPI.init("improperui", ImproperUI.class,
                "scripts/what.ui",
                "scripts/screen.ui"
        );
    }
}
```
**DO NOTE THAT WHEN YOU ACTUALY TRY TO PARSE OR RUN THE SCRIPTS, YOU REFERENCE THE SCRIPT FILE'S NAME NOT THE PATH DECLARED HERE!**

<br>

### Events
To listen to events declared from your script, create a new class that implements `CallbackListener`.

Create a method that 

1. Has annotation `CallbackHandler`
2. Has parameter that contains the type of event you want to listen for
3. The name of the method should match the declared event from your script

```java
public class CustomCallbacks implements CallbackListener {
    
    @CallbackHandler
    public void sendHelloWorld(MouseEvent e) {
        if (e.input.isDown())
            ChatUtils.sendMessage("Hello World");
    }
}
```

In your script, the event should look like this:

```
element {
    on-click: sendHelloWorld
}
```

Finally, when you declare a `ImproperUIPanel` screen, register the callback to your panel:

```java
public void openScreen() {
    ImprperUIPanel panel = new ImprperUIPanel();
    panel.registerCallback(new CustomCallback());
    // parse script and add children elements here
    // panel.addChild()
    MinecraftClient.getInstance().setScreen(panel);
}
```

If you are running a script, provide your custom callback in the creation arguments:

```java
public void openScreen() {
    ImproperUIAPI.parseAndRunFile("testing.ui", new CustomCallback() /* and more... */);
}
```

### Helper Methods
```yml
Helper Methods:
  - ImproperUIPanel.collect() // a list of all elements and widgets, even their children
  - ImproperUIPanel.collectOrdered() // a sorted list based on z-index, of all elements and widgets including their children
  - ImproperUIPanel.collectById() // a list of elements with specified ID
  - ImproperUIPanel.collectByClassAttribute() // a list of elements with specified class attribute
  - ImproperUIPanel.collectById() // first element with specified ID
  - ImproperUIPanel.collectByClassAttribute() // first element with specified class attribute

API:
  - ImproperUIAPI.parse() // parses a script then returns all parsed result elements
  - ImproperUIAPI.parseAndRunFile() // parses a registered script file NAME (NOT PATH) from init() and opens the screen with the elements 
  - ImproperUIAPI.parseAndRunScript() // parses a registered script from init() and opens the screen with the elements
  - ImproperUIAPI.reload() // reloads the API
  - ImproperUIAPI.reInit() // re-init the API with possibly a different Mod ID
```

### Config Keys
A config key consists of three parts: `modId, confileFile, propertyName`. They are used for
saving configuration values for, let's say, your sliders and checkboxes. 

To use a config key in your script, simply type out all three parts **WITH NO SPACES AND SEPARATED BY A COLON (:)**.
Then use that string as an attribute class for the element:

```txt
slider #someId -yourModId:config.properties:testing-slider-value -someAnotherAttributeClass {
    
}
```
This creates a slider that sets and saves values do the config.

### This Project is Never Complete
So pull request your ideas!
Star us to receive latest interesting updates/pull requests!

<br>
<br>
<br>
<br>
<br>

Liked the home screen? This is the script!
```
element {
    size: 100%
    background-color: #40000000
}

element {
    size: 100%
    margin-top: 100%
    shadow-distance: 50%
    shadow-color: white
    opacity: 0.5
}

element {
    background-color: black
    background-clip: padding
    border-thickness: 1
    border-color: white
    border-radius: 5
    shadow-distance: 5

    width: 200
    height: 100
    padding: 10
    padding-left: 50
    padding-right: 50

    draggable: true
    center: both



    element {
        inner-text: "ImproperUI Interactives v0.0.1"
        width: 100%
        text-align: center
        center: both
    }
    element {
        inner-text: "The Ultimate Solution to Rendering Being Too Difficult to Learn"
        text-scale: 0.8

        margin-top: 15
        width: 100%
        text-align: center
        center: both
    }
    element {
        inner-text: ">> &7Click Me to send Hello World&r <<"
        text-scale: 0.8

        margin-top: 45
        width: 150
        border-radius: 5
        background-color: green
        padding: 7
        text-align: center
        center: both

        on-click: sendHelloWorld
        draggable: true
    }


    element {
        width: 100%
        height: 40
        opacity: 0
        child-align: grid
        grid-columns: 10
        background-clip: padding
        scrollable: true

        element {
            size: 20
            border-radius: 50
            background-color: red
            center: horizontal
            background-image: minecraft:textures/item/iron_pickaxe.png
            draggable: true
        }
        element {
            size: 20
            border-radius: 50
            background-color: aqua
            center: horizontal
            background-image: minecraft:textures/item/diamond_sword.png
            draggable: true
        }
        element {
            size: 20
            border-radius: 50
            background-color: yellow
            center: horizontal
            background-image: minecraft:textures/item/golden_axe.png
            draggable: true
        }
        element {
            size: 20
            border-radius: 50
            background-color: lime
            center: horizontal
            background-image: minecraft:textures/item/apple.png
            draggable: true
        }
        element {
            size: 20
            border-radius: 50
            background-color: white
            center: horizontal
            background-image: minecraft:textures/item/arrow.png
            draggable: true
        }
        element {
            size: 20
            border-radius: 50
            background-color: orange
            center: horizontal
            background-image: minecraft:textures/block/bedrock.png
            draggable: true
        }
        element {
            size: 20
            border-radius: 50
            background-color: orange
            center: horizontal
            background-image: minecraft:textures/block/torch.png
            draggable: true
        }
        element {
            size: 20
            border-radius: 50
            background-color: white
            center: horizontal
            background-image: minecraft:textures/block/diamond_ore.png
            draggable: true
        }
        element {
            size: 20
            border-radius: 50
            background-color: lime
            center: horizontal
            background-image: minecraft:textures/block/gold_block.png
            draggable: true
        }
        element {
            size: 20
            border-radius: 50
            background-color: lime
            center: horizontal
            background-image: minecraft:textures/block/netherrack.png
            draggable: true
        }
    }
}
```
