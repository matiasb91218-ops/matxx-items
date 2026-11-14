package dev.matxx.items;

import dev.matxx.items.mixin.HandledScreenAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

/**
 * Matxx Items
 *
 * Con una pantalla de inventario abierta, mientras mantenés SHIFT IZQUIERDO apretado y pasás
 * el mouse por encima de un ítem:
 *   - si es un tótem (en tu inventario/hotbar) -> va a la segunda mano (offhand)
 *   - cualquier otro ítem -> se mueve como un shift+click normal
 *     (inventario -> hotbar, hotbar -> inventario, o hacia/desde un cofre, etc.)
 *
 * Cada slot se procesa una sola vez por cada vez que mantenés shift, para que los ítems
 * no "reboten" de un lado a otro.
 */
public class MatxxItems implements ClientModInitializer {

    private static final int OFFHAND_BUTTON = 40;      // botón de SWAP que representa la segunda mano
    private static final int MAIN_INVENTORY_SIZE = 36; // slots 0-35 del PlayerInventory (hotbar + mochila)

    private static final Set<Integer> visited = new HashSet<>();
    private static Screen trackedScreen = null;

    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof HandledScreen<?>) || screen instanceof CreativeInventoryScreen) {
                return;
            }
            // AFTER_INIT se vuelve a llamar al cambiar el tamaño de la ventana: registramos una sola vez por pantalla.
            if (screen == trackedScreen) {
                return;
            }
            trackedScreen = screen;
            visited.clear();

            ScreenEvents.afterRender(screen).register((s, drawContext, mouseX, mouseY, tickDelta) ->
                    onRender(client, (HandledScreen<?>) s));
            ScreenEvents.remove(screen).register(s -> {
                if (trackedScreen == s) {
                    trackedScreen = null;
                }
                visited.clear();
            });
        });
    }

    private static void onRender(MinecraftClient client, HandledScreen<?> screen) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.interactionManager == null) {
            return;
        }

        boolean shiftDown = InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT);
        if (!shiftDown) {
            visited.clear();
            return;
        }

        // No actuar si estás escribiendo (por ejemplo renombrando en el yunque).
        if (screen.getFocused() instanceof TextFieldWidget) {
            return;
        }

        ScreenHandler handler = screen.getScreenHandler();

        // Si tenés un ítem agarrado con el cursor, no tocar nada.
        if (!handler.getCursorStack().isEmpty()) {
            return;
        }

        Slot slot = ((HandledScreenAccessor) screen).matxxitems$getFocusedSlot();
        if (slot == null || !slot.hasStack()) {
            return;
        }

        // Los resultados de crafteo no se tocan (evita craftear sin querer al pasar el mouse).
        if (slot.inventory instanceof CraftingResultInventory) {
            return;
        }

        // Ya lo procesamos mientras mantenés este shift.
        if (!visited.add(slot.id)) {
            return;
        }

        boolean isTotem = slot.getStack().isOf(Items.TOTEM_OF_UNDYING);
        boolean inMainInventory = slot.inventory instanceof PlayerInventory && slot.getIndex() < MAIN_INVENTORY_SIZE;

        if (isTotem && inMainInventory) {
            // Si ya tenés un tótem en la segunda mano lo dejamos quieto.
            if (!player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
                client.interactionManager.clickSlot(handler.syncId, slot.id, OFFHAND_BUTTON, SlotActionType.SWAP, player);
            }
            return;
        }

        // Todo lo demás: shift+click normal (arriba <-> abajo).
        client.interactionManager.clickSlot(handler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, player);
    }
}
