# SaveBanner
A minecraft plugin to save and load banners!

Only tested on Minecraft version 1.20.4. Compiled with Java 17.


## Commands:
```
- /banner help
- /banner list
- /banner save <name>
- /banner load <name>
- /banner delete <name>
- /banner reload
```
**Commands aliases:** 
- `/savebanner`
- `/banner`
- `/sb`



## Permissions:
```
savebanner.use - Permission to use SaveBanner.
```


## Configuration:
### config.yml
The general config of SaveBanner.
```
# The prefix of the plugin. Should end with "&r ".
prefix: "&7[&dSaveBanner&7]&r "
```

### banners.yml
Storage for banners. It's not recommended that you edit this file yourself.
```
# Where banners are stored.
# It's not recommended to edit this file.

savedBanners:
    example:
        ==: org.bukkit.inventory.ItemStack
        v: 3700
        type: BLACK_BANNER
        meta:
            ==: ItemMeta
            meta-type: BANNER
            patterns:
                - ==: Pattern
                  color: LIME
                  pattern: cre
```


## Screenshots:
![Help menu](assets/screenshots/helpmenu.png)

![Saved banner list](assets/screenshots/savedbannerlist.png)

![Load banner message](assets/screenshots/loadbanner.png)
