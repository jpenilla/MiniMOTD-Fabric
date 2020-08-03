package xyz.jpenilla.minimotdfabric;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.text.Text;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@ConfigSerializable
public class Config {
    private static ObjectMapper<Config> MAPPER;

    static {
        try {
            MAPPER = ObjectMapper.forClass(Config.class);
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    @Setting
    private Motd motd = Motd.getDefaultMotd();
    @Setting
    private MaxPlayers maxPlayers = new MaxPlayers();
    @Setting
    private FakePlayers fakePlayers = new FakePlayers();

    public static Config loadFrom(CommentedConfigurationNode node) throws ObjectMappingException {
        return MAPPER.bindToNew().populate(node);
    }

    public MaxPlayers getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(MaxPlayers maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public FakePlayers getFakePlayers() {
        return fakePlayers;
    }

    public void setFakePlayers(FakePlayers fakePlayers) {
        this.fakePlayers = fakePlayers;
    }

    public void saveTo(CommentedConfigurationNode node) throws ObjectMappingException {
        MAPPER.bind(this).serialize(node);
    }

    public Motd getMotd() {
        return motd;
    }

    public void setMotd(Motd motd) {
        this.motd = motd;
    }

    @ConfigSerializable
    public static class Motd {
        private final GsonComponentSerializer gsonComponentSerializer = GsonComponentSerializer.gson();
        private final GsonComponentSerializer legacyGsonComponentSerializer = GsonComponentSerializer.colorDownsamplingGson();
        @Setting(value = "motds", comment = "The list of MotDs to use\n" +
                "  Placeholders: {onlinePlayers} {maxPlayers}\n" +
                "  Use {br} to separate lines.\n" +
                "  Putting more than one will cause one to be randomly chosen each refresh")
        private final List<String> motds = new ArrayList<>();
        @Setting(value = "motd-enabled", comment = "Enable/Disable changing the MotD")
        private boolean enabled = true;

        public static Motd getDefaultMotd() {
            final Motd motd = new Motd();
            motd.getMotds().add("<white><rainbow>|||||||||||||||||||||||||||||||||||||</rainbow>     A Fabric Server     <rainbow>|||||||||||||||||||||||||||||||||||||</rainbow>{br}                   {onlinePlayers} <blue>/</blue> {maxPlayers} Players Online");
            motd.getMotds().add("<white><gradient:blue:green>|||||||||||||||||||||||||||||||||||||</gradient>     A Fabric Server     <gradient:green:blue>|||||||||||||||||||||||||||||||||||||</gradient>{br}                   {onlinePlayers} <blue>/</blue> {maxPlayers} Players Online");
            motd.getMotds().add("<white><gradient:red:blue:red>|||||||||||||||||||||||||||||||||||||</gradient>     A Fabric Server     <gradient:blue:red:blue>|||||||||||||||||||||||||||||||||||||</gradient>{br}                   {onlinePlayers} <blue>/</blue> {maxPlayers} Players Online");
            motd.getMotds().add("<white><gradient:green:yellow>|||||||||||||||||||||||||||||||||||||</gradient>     A Fabric Server     <gradient:yellow:green>|||||||||||||||||||||||||||||||||||||</gradient>{br}                   {onlinePlayers} <blue>/</blue> {maxPlayers} Players Online");
            return motd;
        }

        public List<String> getMotds() {
            return motds;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Text getMotd(int max, int online, int clientProtocolVersion) {
            Component component = MiniMessage.get().parse(motds.get(ThreadLocalRandom.current().nextInt(motds.size()))
                    .replace("{onlinePlayers}", String.valueOf(online))
                    .replace("{maxPlayers}", String.valueOf(max))
                    .replace("{br}", "\n"));
            if (clientProtocolVersion > 700) {
                return Text.Serializer.fromJson(gsonComponentSerializer.serialize(component));
            } else {
                return Text.Serializer.fromJson(legacyGsonComponentSerializer.serialize(component));
            }
        }
    }

    @ConfigSerializable
    public static class MaxPlayers {
        @Setting(value = "max-players-enabled", comment = "Enable/Disable changing the Max Players number displayed")
        private boolean enabled = true;
        @Setting(value = "max-players", comment = "Set the Max Players")
        private int maxPlayers = 69;
        @Setting(value = "just-x-more-enabled", comment = "Changes the Max Players to X more than the online players\nExample: 16/19 players online.")
        private boolean xMoreEnabled = false;
        @Setting(value = "x-value", comment = "Set the x value for the just-x-more-enabled setting")
        private int xValue = 3;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getMaxPlayers() {
            return maxPlayers;
        }

        public void setMaxPlayers(int maxPlayers) {
            this.maxPlayers = maxPlayers;
        }

        public boolean isxMoreEnabled() {
            return xMoreEnabled;
        }

        public void setxMoreEnabled(boolean xMoreEnabled) {
            this.xMoreEnabled = xMoreEnabled;
        }

        public int getxValue() {
            return xValue;
        }

        public void setxValue(int xValue) {
            this.xValue = xValue;
        }

        public int getAdjustedMaxPlayers(int onlinePlayers, int actualMaxPlayers) {
            if (enabled) {
                return xMoreEnabled ? onlinePlayers + xValue : maxPlayers;
            } else {
                return actualMaxPlayers;
            }
        }
    }

    @ConfigSerializable
    public static class FakePlayers {
        @Setting(value = "fake-players-enabled", comment = "Should fake players be added to the online players total")
        private boolean enabled = false;
        @Setting(value = "fake-players",
                comment = "Modes: static, random, percent\n" +
                        "    static: This many fake players will be added\n" +
                        "      ex: fakePlayers: \"3\"\n" +
                        "    random: A random number of fake players in this range will be added\n" +
                        "      ex: fakePlayers: \"3:6\"\n" +
                        "    percent: The player count will be inflated by this much, rounding up\n" +
                        "      ex: fakePlayers: \"25%\"")
        private String fakePlayers = "25%";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getFakePlayers() {
            return fakePlayers;
        }

        public void setFakePlayers(String fakePlayers) {
            this.fakePlayers = fakePlayers;
        }

        public int fakePlayers(int onlinePlayers) {
            int i;
            try {
                if (fakePlayers.contains(":")) {
                    final String[] f = fakePlayers.split(":");
                    final int start = Integer.parseInt(f[0]);
                    final int end = Integer.parseInt(f[1]);
                    i = onlinePlayers + ThreadLocalRandom.current().nextInt(start, end);
                } else if (fakePlayers.contains("%")) {
                    final double factor = 1 + (Double.parseDouble(fakePlayers.replace("%", "")) / 100);
                    i = (int) Math.ceil(factor * onlinePlayers);
                } else {
                    final int addedPlayers = Integer.parseInt(fakePlayers);
                    i = onlinePlayers + addedPlayers;
                }
            } catch (NumberFormatException ex) {
                System.out.println("[MiniMOTD] fakePlayers config incorrect");
                i = 0;
            }
            return i;
        }
    }
}
