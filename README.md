# MvInv Importer
Also see the plugin page on the spigot website: https://www.spigotmc.org/resources/mvinv-importer.85353/

I was migrating my Multiverse Server to a Bungeecord setup and did not find anything that helps with migrating inventories and XP, so I coded a small plugin myself.

## Overview
This plugin can read the .json files created by Multiverse Inventories and apply their content to players.

## Installation and configuration
Drop the .jar into your plugins folder, restart your server. There is currently no configuration possible.

## Usage
Copy the json files you want to import into the plugin's directory. Use the command `/mvinvimporter set filename.json playername` to apply the content of "filename.json" to the player "playername". You can ommit the player name, then the plugin will use the filename (minus ".json") as the player name.

## Commands and permissions
`/mvinvimporter set [filename] [playername]` sets the content of "filename" to player named "playername". Needs the permission **mvinvimporter.set**.

## Shortcomings
* Currently only takes into account survival inventories
* Ignores location and spawns
* Ignores potion effects
* Ignores a few stats like fall distance, fire ticks and flood level
