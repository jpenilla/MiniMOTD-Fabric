package xyz.jpenilla.minimotdfabric;

import net.fabricmc.api.ModInitializer;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
import java.io.IOException;

public class MiniMOTDFabric implements ModInitializer {
    public static MiniMOTDFabric INSTANCE;
    private int protocolVersionCache;
    private Config config;

    public int getProtocolVersionCache() {
        return protocolVersionCache;
    }

    public void setProtocolVersionCache(int protocolVersionCache) {
        this.protocolVersionCache = protocolVersionCache;
    }

    public Config getConfig() {
        return config;
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setFile(new File("minimotd.conf")).build();
        try {
            config = Config.loadFrom(loader.load());
            CommentedConfigurationNode node = CommentedConfigurationNode.root();
            config.saveTo(node);
            loader.save(node);
        } catch (ObjectMappingException | IOException e) {
            e.printStackTrace();
        }
    }
}
