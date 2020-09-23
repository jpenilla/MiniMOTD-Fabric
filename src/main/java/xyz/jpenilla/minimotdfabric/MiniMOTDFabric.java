package xyz.jpenilla.minimotdfabric;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.kyori.adventure.platform.fabric.FabricAudienceProvider;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MiniMOTDFabric implements ModInitializer {

    public static MiniMOTDFabric INSTANCE;
    public static final Logger LOGGER = LogManager.getLogger("MiniMOTD");
    private final MiniMessage miniMessage = MiniMessage.get();
    private Config config;
    private final List<String> faviconCache = new ArrayList<>();
    private int protocolVersionCache;

    public int getProtocolVersionCache() {
        return protocolVersionCache;
    }

    public void setProtocolVersionCache(int protocolVersionCache) {
        this.protocolVersionCache = protocolVersionCache;
    }

    public Config getConfig() {
        return config;
    }

    public boolean hasFavicon() {
        return !faviconCache.isEmpty();
    }

    public String getRandomFavicon() {
        return faviconCache.get(ThreadLocalRandom.current().nextInt(faviconCache.size()));
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing MiniMOTD...");
        INSTANCE = this;
        loadConfig();
        loadIcons();
        registerCommand();
        LOGGER.info("Done initializing MiniMOTD.");
    }

    private void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
                CommandManager.literal("minimotd")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(CommandManager.literal("reload")
                                .executes(ctx -> {
                                    send(ctx.getSource(), true, "<white>[<gradient:blue:aqua>MiniMOTD</gradient>] <italic><gray>Reloading MiniMOTD...");
                                    loadConfig();
                                    loadIcons();
                                    send(ctx.getSource(), true, "<white>[<gradient:blue:aqua>MiniMOTD</gradient>] <green>Done reloading MiniMOTD.");
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("about")
                                .executes(ctx -> {
                                    send(ctx.getSource(), false,
                                            "<strikethrough><gradient:black:white>------------------",
                                            "<hover:show_text:'<gradient:blue:aqua>click me!'><click:open_url:https://github.com/jmanpenilla/MiniMOTD-Fabric>    MiniMOTD-Fabric",
                                            "<gray>      By <gradient:gold:yellow>jmp",
                                            "<strikethrough><gradient:black:white>------------------"
                                    );
                                    return 1;
                                })
                        )
        ));
    }

    private void send(ServerCommandSource c, boolean broadcastToOps, String... strings) {
        for (String s : strings) {
            c.sendFeedback(FabricAudienceProvider.adapt(miniMessage.parse(s)), broadcastToOps);
        }
    }

    private void loadConfig() {
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setFile(new File("minimotd.conf")).build();
        try {
            config = Config.loadFrom(loader.load());
            CommentedConfigurationNode node = CommentedConfigurationNode.root();
            config.saveTo(node);
            loader.save(node);
            LOGGER.info("Successfully loaded minimotd.conf");
        } catch (ObjectMappingException | IOException e) {
            LOGGER.warn("Failed to load minimotd.conf", e);
        }
    }

    private void loadIcons() {
        faviconCache.clear();
        File iconsFolder = new File("server-icons" + File.separator);
        if (!iconsFolder.exists()) {
            iconsFolder.mkdir();
        }
        File[] files = iconsFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    ByteBuf byteBuf = Unpooled.buffer();
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        Validate.validState(bufferedImage.getWidth() == 64, "Must be 64 pixels wide");
                        Validate.validState(bufferedImage.getHeight() == 64, "Must be 64 pixels high");
                        ImageIO.write(bufferedImage, "PNG", new ByteBufOutputStream(byteBuf));
                        ByteBuffer byteBuffer = Base64.getEncoder().encode(byteBuf.nioBuffer());
                        faviconCache.add("data:image/png;base64," + StandardCharsets.UTF_8.decode(byteBuffer));
                        LOGGER.printf(Level.INFO, "Successfully loaded image: server-icons/%s", file.getName());
                    } catch (Exception e) {
                        LOGGER.printf(Level.WARN, "Failed to read image: server-icons/%s ensure it is a png and is 64x64 pixels in size.", file.getName());
                    } finally {
                        byteBuf.release();
                    }
                }
            }
        }
        LOGGER.printf(Level.INFO, "Loaded %s server icon files.", faviconCache.size());
    }
}
