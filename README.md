# Benchkit
<!-- [![HitCount](http://hits.dwyl.com/lukeeey/benchkit.svg)](http://hits.dwyl.com/lukeeey/benchkit)  -->
A Blockbench plugin and a Minecraft server plugin to facilitate with testing Minecraft: Bedrock Edition skins.

![Benchkit](https://github.com/lukeeey/benchkit/workflows/Benchkit/badge.svg)
[![Discord](https://img.shields.io/discord/803794932820082739.svg?color=%237289da&label=Discord)](https://discord.gg/wS7ZpJcMtZ)

## Usage
This process will become simpler as the plugins become more developed.

1. Install the plugin on a Nukkit server
2. Run the server, stop it, and edit the config (making sure to change the `key` value unless you're just testing)
3. Launch Blockbench, drag and drop the `benchkit.js` file into the page (it will import the plugin)
4. Click the `Benchkit` menu at the top of the screen and select `Connect to Minecraft Server` and enter the details (keybind coming soon) 
5. Once connected, go about editng your skin and then click `Apply Skin on Server` in the `Benchkit` menu (next time you want to apply the skin to the same player just hit `CTRL+SHIFT+L`)
6. Done! Your skin will change in game instantly

## Planned Features
* "Groups" support
  * Will be able to sort players into groups either by selecting multiple UUIDs or based on custom criteria (**maybe**)
* Apply a skin or model and have the option to apply the other one at the same time
  * **Example:** You go to apply a skin and in the dialog it gives you an option to choose from one of your previously saved models
  * This feature is just intended to save time
