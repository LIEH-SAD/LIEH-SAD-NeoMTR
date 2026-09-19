package cn.zbx1425.mtrsteamloco.data;

import mtr.data.Rail;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public interface RailExtraSupplier {

    boolean getIsSecondaryDir();

    void setIsSecondaryDir(boolean value);

    float getVerticalCurveRadius();

    void setVerticalCurveRadius(float value);

    int getHeight();

    List<RailModelRepeater> getRepeaters();

    void setRepeaters(List<RailModelRepeater> repeaters);

    /**
     * This fork renders the secondary direction of a rail instead of flipping the models,
     * so the ANTE "render reversed" flag maps onto {@link #setIsSecondaryDir(boolean)}.
     */
    default boolean getRenderReversed() {
        return !getIsSecondaryDir();
    }

    default void setRenderReversed(boolean value) {
        setIsSecondaryDir(!value);
    }

    /**
     * Copies the per-rail model configuration from another rail. Distances stored in
     * {@link RailModelRepeater} are already in block units, so no rescaling is needed.
     */
    default void partialCopyFrom(Rail rail) {
        final RailExtraSupplier other = (RailExtraSupplier) rail;
        List<RailModelRepeater> copied = new ArrayList<>();
        for (RailModelRepeater repeater : other.getRepeaters()) {
            copied.add(repeater.copy());
        }
        setRepeaters(copied);
        setVerticalCurveRadius(other.getVerticalCurveRadius());
    }

    static float getVTheta(Rail rail, double verticalCurveRadius) {
        double H = Math.abs(((RailExtraSupplier)rail).getHeight());
        double L = rail.getLength();
        double R = verticalCurveRadius;
        return 2 * (float) Mth.atan2(Math.sqrt(H * H - 4 * R * H + L * L) - L, H - 4 * R);
    }

    /**
     * Roll (superelevation) is not modelled by this fork, so sliced blocks are never rolled.
     * Kept so {@code SliceTask.useRoll} remains source compatible with upstream ANTE.
     */
    static float getRollAngle(Rail rail, double value) {
        return 0F;
    }

}
