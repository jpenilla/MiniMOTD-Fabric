# MiniMOTD-Fabric

> This repo is archived as it has been replaced by the fabric module of [MiniMOTD](https://github.com/jpenilla/MiniMOTD) 

MiniMOTD-Fabric is a server-side only Fabric mod for Minecraft that allows easily changing the server list motd with support for RGB, gradients, and more.

![image](https://i.imgur.com/1YU9iqG.png)

Example minimotd.conf
```
fakePlayers {
    # Modes: static, random, percent
    #    static: This many fake players will be added
    #      ex: fakePlayers: "3"
    #    random: A random number of fake players in this range will be added
    #      ex: fakePlayers: "3:6"
    #    percent: The player count will be inflated by this much, rounding up
    #      ex: fakePlayers: "25%"
    fake-players="50:99"
    # Should fake players be added to the online players total
    fake-players-enabled=true
}
maxPlayers {
    # Changes the Max Players to X more than the online players
    # Example: 16/19 players online.
    just-x-more-enabled=false
    # Set the Max Players
    max-players=100
    # Enable/Disable changing the Max Players number displayed
    max-players-enabled=true
    # Set the x value for the just-x-more-enabled setting
    x-value=10
}
motd {
    # Enable/Disable changing the MotD
    motd-enabled=true
    # The list of MotDs to use
    #  Placeholders: {onlinePlayers} {maxPlayers}
    #  Use {br} to separate lines.
    #  Putting more than one will cause one to be randomly chosen each refresh
    motds=[
        "<white><rainbow>|||||||||||||||||||||||||||||||||||||</rainbow>     A Fabric Server     <rainbow>|||||||||||||||||||||||||||||||||||||</rainbow>{br}                   {onlinePlayers} <blue>/</blue> {maxPlayers} Players Online",
        "<white><gradient:blue:green>|||||||||||||||||||||||||||||||||||||</gradient>     A Fabric Server     <gradient:green:blue>|||||||||||||||||||||||||||||||||||||</gradient>{br}                   {onlinePlayers} <blue>/</blue> {maxPlayers} Players Online",
        "<white><gradient:red:blue>|||||||||||||||||||||||||||||||||||||</gradient>     A Fabric Server     <gradient:blue:red>|||||||||||||||||||||||||||||||||||||</gradient>{br}                   {onlinePlayers} <blue>/</blue> {maxPlayers} Players Online",
        "<white><gradient:green:yellow>|||||||||||||||||||||||||||||||||||||</gradient>     A Fabric Server     <gradient:yellow:green>|||||||||||||||||||||||||||||||||||||</gradient>{br}                   {onlinePlayers} <blue>/</blue> {maxPlayers} Players Online",
        "<white><gradient:red:blue:red>|||||||||||||||||||||||||||||||||||||</gradient>     A Fabric Server     <gradient:blue:red:blue>|||||||||||||||||||||||||||||||||||||</gradient>{br}                   {onlinePlayers} <blue>/</blue> {maxPlayers} Players Online"
    ]
}
```
