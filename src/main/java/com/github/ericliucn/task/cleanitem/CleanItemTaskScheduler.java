package com.github.ericliucn.task.cleanitem;

import com.github.ericliucn.Main;
import com.github.ericliucn.config.Config;
import com.github.ericliucn.utils.Utils;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CleanItemTaskScheduler {

    private final Task task;
    private ServerBossBar.Builder bossBar;
    private final List<ServerBossBar> bars = new ArrayList<>();

    public CleanItemTaskScheduler() throws ObjectMappingException {

        final int[] next = {Config.interval};

        Runnable runnable = () -> {

            Main.NEXT_CLEAN_ITEM_TIME = next[0];

            if (next[0] == 0){

                try {
                    new CleanItemTask();
                } catch (ObjectMappingException e) {
                    e.printStackTrace();
                }

                next[0] = Config.interval;

                this.bars.forEach(bars -> bars.setVisible(false));
                this.bars.clear();
            }else {

                if (Config.notifyTime.contains(next[0])) {
                    Utils.broadCastWithPapi(Config.msg_notify, false);
                    if (Config.soundWhenNotify) {
                        Utils.playSoundForEveryone(SoundTypes.BLOCK_STONE_BUTTON_CLICK_ON);
                    }
                }

                if (Config.enableBossBar && next[0] <= Config.startBossBar){
                    float numerator = next[0];
                    float denominator = Config.startBossBar;
                    float percent = numerator/denominator;
                    Collection<Player> players = Sponge.getServer().getOnlinePlayers();
                    if (this.bars.size() < players.size()) {
                        this.bars.forEach(serverBossBar -> serverBossBar.setVisible(false));
                        this.bars.clear();
                        this.setupBossBar();
                        Sponge.getServer().getOnlinePlayers().forEach(player -> {
                            ServerBossBar bar = bossBar
                                    .name(Utils.papiReplace(Config.msg_bar_title, player, player))
                                    .visible(true)
                                    .percent(percent)
                                    .build()
                                    .addPlayer(player);
                            this.bars.add(bar);
                        });
                    }else {
                        bars.forEach(serverBossBar -> {
                            Player player = (Player) serverBossBar.getPlayers().toArray()[0];
                            serverBossBar.setName(Utils.papiReplace(Config.msg_bar_title, player, player));
                            serverBossBar.setPercent(percent);
                        });
                    }
                }

                next[0]--;
            }
        };

        task = Sponge.getScheduler()
                .createTaskBuilder()
                .execute(runnable)
                .interval(1, TimeUnit.SECONDS)
                .name("EClean Clean Items Task")
                .submit(Main.instance);

    }

    private void setupBossBar(){

        BossBarColor barColor;

        switch (Config.barColor){
            case "BLUE":
                barColor = BossBarColors.BLUE;
                break;
            case "RED":
                barColor = BossBarColors.RED;
                break;
            case "GREEN":
                barColor = BossBarColors.GREEN;
                break;
            case "WHITE":
                barColor = BossBarColors.WHITE;
                break;
            case "PINK":
                barColor = BossBarColors.PINK;
                break;
            case "YELLOW":
                barColor = BossBarColors.YELLOW;
                break;
            default:
                barColor = BossBarColors.PURPLE;
                break;
        }

        bossBar = ServerBossBar.builder()
                .visible(true)
                .overlay(BossBarOverlays.PROGRESS)
                .color(barColor);
    }

    public void cancel(){
        this.task.cancel();
        this.bars.forEach(serverBossBar -> serverBossBar.setVisible(false));
    }


}
