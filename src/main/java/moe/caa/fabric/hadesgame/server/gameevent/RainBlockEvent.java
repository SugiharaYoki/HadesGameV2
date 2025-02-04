package moe.caa.fabric.hadesgame.server.gameevent;

import moe.caa.fabric.hadesgame.server.GameCore;
import moe.caa.fabric.hadesgame.server.HadesGame;
import moe.caa.fabric.hadesgame.server.schedule.AbstractTick;
import moe.caa.fabric.hadesgame.server.schedule.HadesGameScheduleManager;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Heightmap;

import java.util.ArrayList;
import java.util.List;

public class RainBlockEvent extends ImplicitAbstractEvent {

    private final List<AbstractTick> eventTick = new ArrayList<>();

    public RainBlockEvent() {
        super("rainBlock", "方块雨", true, 30, 100);
    }

    @Override
    public void callEvent() {
        AbstractTick tick = new AbstractTick(20) {
            private int count = 0;

            @Override
            protected void tick() {
                count++;
                GameCore.INSTANCE.survivalPlayerHandler(player -> generateBlock(player));
                if (count > 60) cancel();
            }
        };

        HadesGameScheduleManager.runTaskTimer(tick);
        eventTick.add(tick);
    }

    public void generateBlock(ServerPlayerEntity entity) {
        ServerWorld world = entity.getServerWorld();
        int spawnY = (int) Math.max(entity.getY() + 10, world.getTopY(Heightmap.Type.MOTION_BLOCKING, (int) entity.getX(), (int) entity.getZ()) + 10);
        if (spawnY > entity.getY() + 50) return;


        FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(world, entity.getX(), spawnY, entity.getZ(), HadesGame.BLOCK_WEIGHT_RANDOM_ARRAY_LIST.randomGet().getDefaultState());
        fallingBlockEntity.timeFalling = 1;

        world.spawnEntity(fallingBlockEntity);

    }

    @Override
    public void gameEnd() {
        eventTick.forEach(AbstractTick::cancel);
        eventTick.clear();
    }
}
