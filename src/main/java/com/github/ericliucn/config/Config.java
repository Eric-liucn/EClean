package com.github.ericliucn.config;

import com.github.ericliucn.Main;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.asset.Asset;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Config {

    private final static File conf = new File(Main.instance.file,"eclean.conf");
    private final static File message = new File(Main.instance.file,"message.properties");
    private final static ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(conf).build();
    public static Properties msg = new Properties();
    public static CommentedConfigurationNode rootNode;

    //消息
    public static String msg_notify;
    public static String msg_bar_title;
    public static String msg_clean_finished;
    public static String msg_clean_finished_hover;
    public static String msg_inv_title;
    public static String msg_item_has_taken;
    public static String msg_last_task_not_finished;
    public static String msg_block_report;
    public static String msg_block_check_task_start;

    //清理间隔时间
    public static int interval;
    //距离清理多少秒时提醒
    public static List<Integer> notifyTime;
    //是否开启血条模式
    public static boolean enableBossBar;
    //开启血条的时间点
    public static int startBossBar;
    //血条的颜色
    public static String barColor;

    //不需要清理的世界
    public static List<String> skipWorlds;
    //是否跳过含有nbt的物品
    public static boolean skipItemHasNBT;
    //是否跳过含有nbt的物品
    public static boolean skipItemHasLore;
    //需要匹配的lore
    public static List<String> loreMatch;
    //跳过的物品
    public static List<String> skipItems;
    //移除物品动画
    public static boolean particleEffect;
    //提醒时的音效
    public static boolean soundWhenNotify;
    //Mod支持
    public static boolean modSupport;

    //需要检测更新频率的方块
    public static Map<String, Double> blocksNeedWatch = new HashMap<>();
    //是否开启自动检测方块
    public static boolean isEnableCleanBlock;
    //是否清理高频方块
    public static boolean cleanBlock;
    //检测间隔
    public static int cleanBlockInterval;
    //黑名单模式
    public static boolean blackListMode;
    //黑名单检测刷新限制
    public static double blackListModeTickRate;


    public static void init() throws IOException, ObjectMappingException {
        if (!Main.instance.file.exists()){
            Main.instance.file.mkdir();
        }

        Asset assetConf = Main.instance.pluginContainer.getAsset("eclean.conf").get();
        Asset assetMessage = Main.instance.pluginContainer.getAsset("message.properties").get();

        if (!conf.exists()){
            assetConf.copyToDirectory(Main.instance.file.toPath());
        }

        if (!message.exists()){
            assetMessage.copyToDirectory(Main.instance.file.toPath());
        }

        load();
    }

    public static void load() throws IOException, ObjectMappingException {
        rootNode = loader.load();
        msg.load(new InputStreamReader(new FileInputStream(message), StandardCharsets.UTF_8));

        interval = rootNode.getNode("ItemClean", "CleanItemInterval").getInt();
        notifyTime = rootNode.getNode("ItemClean", "CleanNotify").getList(TypeToken.of(Integer.class));
        enableBossBar = rootNode.getNode("ItemClean", "NotifyBossBar", "Enable").getBoolean();
        startBossBar = rootNode.getNode("ItemClean", "NotifyBossBar", "Start").getInt();
        skipWorlds = rootNode.getNode("ItemClean", "Filter", "Wolrds").getList(TypeToken.of(String.class));
        skipItemHasNBT = rootNode.getNode("ItemClean", "Filter", "SkipItemWithNBT").getBoolean();
        skipItemHasLore = rootNode.getNode("ItemClean", "Filter", "SkipItemWithLore").getBoolean();
        loreMatch = rootNode.getNode("ItemClean", "Filter", "LoreMatch").getList(TypeToken.of(String.class));
        skipItems = rootNode.getNode("ItemClean", "Filter", "Items").getList(TypeToken.of(String.class));
        barColor = rootNode.getNode("ItemClean", "NotifyBossBar", "Color").getString();
        particleEffect = rootNode.getNode("ItemClean", "ParticleEffectWhenItemRemove").getBoolean();
        soundWhenNotify = rootNode.getNode("ItemClean", "SoundWhenNotify").getBoolean();
        modSupport = rootNode.getNode("ItemClean", "ModSupport").getBoolean();

        blocksNeedWatch.clear();
        rootNode.getNode("CheckBlock", "Blocks").getList(TypeToken.of(String.class)).forEach(s -> {
            String[] strings = s.split(",");
            blocksNeedWatch.put(strings[0], Double.parseDouble(strings[1]));
        });
        isEnableCleanBlock = rootNode.getNode("CheckBlock", "Enable").getBoolean();
        cleanBlock = rootNode.getNode("CheckBlock", "ClearBlock").getBoolean();
        cleanBlockInterval = rootNode.getNode("CheckBlock", "Interval").getInt();
        blackListMode = rootNode.getNode("CheckBlock", "BlackListMode").getBoolean();
        blackListModeTickRate = rootNode.getNode("CheckBlock", "BlackListModeTickRate").getDouble();

        //msg
        msg_notify = Config.msg.getProperty("CleanNotify");
        msg_bar_title = Config.msg.getProperty("BossBarTitle");
        msg_clean_finished = Config.msg.getProperty("CleanFinished");
        msg_clean_finished_hover = Config.msg.getProperty("CleanFinishedOnHover");
        msg_inv_title = Config.msg.getProperty("InventoryTitle");
        msg_item_has_taken = Config.msg.getProperty("ItemHasBeenTakenByOthers");
        msg_last_task_not_finished = Config.msg.getProperty("LastTaskNotFinished");
        msg_block_report = Config.msg.getProperty("BlockReport");
        msg_block_check_task_start = Config.msg.getProperty("BlockCheckTaskStart");
    }

    public static void save() throws IOException{
        loader.save(rootNode);
    }
}
