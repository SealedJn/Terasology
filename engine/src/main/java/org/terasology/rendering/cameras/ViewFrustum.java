// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.rendering.cameras;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;
import org.terasology.joml.geom.AABBfc;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.CoreRegistry;

import java.nio.FloatBuffer;

/**
 * View frustum usable for frustum culling.
 *
 */
public class ViewFrustum {

    private final FrustumPlane[] planes = new FrustumPlane[6];
    private final FloatBuffer clip = BufferUtils.createFloatBuffer(16);

    /**
     * Init. a new view frustum.
     */
    public ViewFrustum() {
        for (int i = 0; i < 6; i++) {
            planes[i] = new FrustumPlane();
        }
    }

    /**
     * Updates the view frustum using the currently active modelview and projection matrices.
     */
    public void updateFrustum(FloatBuffer modelViewMatrix, FloatBuffer projectionMatrix) {
        clip.put(0, modelViewMatrix.get(0) * projectionMatrix.get(0) + modelViewMatrix.get(1) * projectionMatrix.get(4)
            + modelViewMatrix.get(2) * projectionMatrix.get(8) + modelViewMatrix.get(3) * projectionMatrix.get(12));
        clip.put(1, modelViewMatrix.get(0) * projectionMatrix.get(1) + modelViewMatrix.get(1) * projectionMatrix.get(5)
            + modelViewMatrix.get(2) * projectionMatrix.get(9) + modelViewMatrix.get(3) * projectionMatrix.get(13));
        clip.put(2, modelViewMatrix.get(0) * projectionMatrix.get(2) + modelViewMatrix.get(1) * projectionMatrix.get(6)
            + modelViewMatrix.get(2) * projectionMatrix.get(10) + modelViewMatrix.get(3) * projectionMatrix.get(14));
        clip.put(3, modelViewMatrix.get(0) * projectionMatrix.get(3) + modelViewMatrix.get(1) * projectionMatrix.get(7)
            + modelViewMatrix.get(2) * projectionMatrix.get(11) + modelViewMatrix.get(3) * projectionMatrix.get(15));

        clip.put(4, modelViewMatrix.get(4) * projectionMatrix.get(0) + modelViewMatrix.get(5) * projectionMatrix.get(4)
            + modelViewMatrix.get(6) * projectionMatrix.get(8) + modelViewMatrix.get(7) * projectionMatrix.get(12));
        clip.put(5, modelViewMatrix.get(4) * projectionMatrix.get(1) + modelViewMatrix.get(5) * projectionMatrix.get(5)
            + modelViewMatrix.get(6) * projectionMatrix.get(9) + modelViewMatrix.get(7) * projectionMatrix.get(13));
        clip.put(6, modelViewMatrix.get(4) * projectionMatrix.get(2) + modelViewMatrix.get(5) * projectionMatrix.get(6)
            + modelViewMatrix.get(6) * projectionMatrix.get(10) + modelViewMatrix.get(7) * projectionMatrix.get(14));
        clip.put(7, modelViewMatrix.get(4) * projectionMatrix.get(3) + modelViewMatrix.get(5) * projectionMatrix.get(7)
            + modelViewMatrix.get(6) * projectionMatrix.get(11) + modelViewMatrix.get(7) * projectionMatrix.get(15));

        clip.put(8, modelViewMatrix.get(8) * projectionMatrix.get(0) + modelViewMatrix.get(9) * projectionMatrix.get(4)
            + modelViewMatrix.get(10) * projectionMatrix.get(8) + modelViewMatrix.get(11) * projectionMatrix.get(12));
        clip.put(9, modelViewMatrix.get(8) * projectionMatrix.get(1) + modelViewMatrix.get(9) * projectionMatrix.get(5)
            + modelViewMatrix.get(10) * projectionMatrix.get(9) + modelViewMatrix.get(11) * projectionMatrix.get(13));
        clip.put(10, modelViewMatrix.get(8) * projectionMatrix.get(2) + modelViewMatrix.get(9) * projectionMatrix.get(6)
            + modelViewMatrix.get(10) * projectionMatrix.get(10) + modelViewMatrix.get(11) * projectionMatrix.get(14));
        clip.put(11, modelViewMatrix.get(8) * projectionMatrix.get(3) + modelViewMatrix.get(9) * projectionMatrix.get(7)
            + modelViewMatrix.get(10) * projectionMatrix.get(11) + modelViewMatrix.get(11) * projectionMatrix.get(15));

        clip.put(12,
            modelViewMatrix.get(12) * projectionMatrix.get(0) + modelViewMatrix.get(13) * projectionMatrix.get(4)
            + modelViewMatrix.get(14) * projectionMatrix.get(8) + modelViewMatrix.get(15) * projectionMatrix.get(12));
        clip.put(13,
            modelViewMatrix.get(12) * projectionMatrix.get(1) + modelViewMatrix.get(13) * projectionMatrix.get(5)
            + modelViewMatrix.get(14) * projectionMatrix.get(9) + modelViewMatrix.get(15) * projectionMatrix.get(13));
        clip.put(14,
            modelViewMatrix.get(12) * projectionMatrix.get(2) + modelViewMatrix.get(13) * projectionMatrix.get(6)
            + modelViewMatrix.get(14) * projectionMatrix.get(10) + modelViewMatrix.get(15) * projectionMatrix.get(14));
        clip.put(15,
            modelViewMatrix.get(12) * projectionMatrix.get(3) + modelViewMatrix.get(13) * projectionMatrix.get(7)
            + modelViewMatrix.get(14) * projectionMatrix.get(11) + modelViewMatrix.get(15) * projectionMatrix.get(15));

        // RIGHT
        planes[0].setA(clip.get(3) - clip.get(0));
        planes[0].setB(clip.get(7) - clip.get(4));
        planes[0].setC(clip.get(11) - clip.get(8));
        planes[0].setD(clip.get(15) - clip.get(12));
        planes[0].normalize();

        // LEFT
        planes[1].setA(clip.get(3) + clip.get(0));
        planes[1].setB(clip.get(7) + clip.get(4));
        planes[1].setC(clip.get(11) + clip.get(8));
        planes[1].setD(clip.get(15) + clip.get(12));
        planes[1].normalize();

        // BOTTOM
        planes[2].setA(clip.get(3) + clip.get(1));
        planes[2].setB(clip.get(7) + clip.get(5));
        planes[2].setC(clip.get(11) + clip.get(9));
        planes[2].setD(clip.get(15) + clip.get(13));
        planes[2].normalize();

        // TOP
        planes[3].setA(clip.get(3) - clip.get(1));
        planes[3].setB(clip.get(7) - clip.get(5));
        planes[3].setC(clip.get(11) - clip.get(9));
        planes[3].setD(clip.get(15) - clip.get(13));
        planes[3].normalize();

        // FAR
        planes[4].setA(clip.get(3) - clip.get(2));
        planes[4].setB(clip.get(7) - clip.get(6));
        planes[4].setC(clip.get(11) - clip.get(10));
        planes[4].setD(clip.get(15) - clip.get(14));
        planes[4].normalize();

        // NEAR
        planes[5].setA(clip.get(3) + clip.get(2));
        planes[5].setB(clip.get(7) + clip.get(6));
        planes[5].setC(clip.get(11) + clip.get(10));
        planes[5].setD(clip.get(15) + clip.get(14));
        planes[5].normalize();
    }

    /**
     * Returns true if the given point intersects the view frustum.
     */
    public boolean intersects(double x, double y, double z) {
        for (int i = 0; i < 6; i++) {
            if (planes[i].getA() * x + planes[i].getB() * y + planes[i].getC() * z + planes[i].getD() <= 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if this view frustum intersects the given AABB.
     */
    public boolean intersects(AABBfc aabb) {

        Vector3f cp = CoreRegistry.get(LocalPlayer.class).getViewPosition(new Vector3f());
        for (int i = 0; i < 6; i++) {
            if (planes[i].getA() * (aabb.minX() - cp.x) + planes[i].getB() * (aabb.minY() - cp.y)
                + planes[i].getC() * (aabb.maxZ() - cp.z) + planes[i].getD() > 0) {
                continue;
            }
            if (planes[i].getA() * (aabb.maxX() - cp.x) + planes[i].getB() * (aabb.minY() - cp.y)
                + planes[i].getC() * (aabb.maxZ() - cp.z) + planes[i].getD() > 0) {
                continue;
            }
            if (planes[i].getA() * (aabb.maxX() - cp.x) + planes[i].getB() * (aabb.maxY() - cp.y)
                + planes[i].getC() * (aabb.maxZ() - cp.z) + planes[i].getD() > 0) {
                continue;
            }
            if (planes[i].getA() * (aabb.minX() - cp.x) + planes[i].getB() * (aabb.maxY() - cp.y)
                + planes[i].getC() * (aabb.maxZ() - cp.z) + planes[i].getD() > 0) {
                continue;
            }
            if (planes[i].getA() * (aabb.minX() - cp.x) + planes[i].getB() * (aabb.minY() - cp.y)
                + planes[i].getC() * (aabb.minZ() - cp.z) + planes[i].getD() > 0) {
                continue;
            }
            if (planes[i].getA() * (aabb.maxX() - cp.x) + planes[i].getB() * (aabb.minY() - cp.y)
                + planes[i].getC() * (aabb.minZ() - cp.z) + planes[i].getD() > 0) {
                continue;
            }
            if (planes[i].getA() * (aabb.maxX() - cp.x) + planes[i].getB() * (aabb.maxY() - cp.y)
                + planes[i].getC() * (aabb.minZ() - cp.z) + planes[i].getD() > 0) {
                continue;
            }
            if (planes[i].getA() * (aabb.minX() - cp.x) + planes[i].getB() * (aabb.maxY() - cp.y)
                + planes[i].getC() * (aabb.minZ() - cp.z) + planes[i].getD() > 0) {
                continue;
            }
            return false;
        }

        return true;
    }

    /**
     * Returns true if the given sphere intersects the given AABB.
     */
    public boolean intersects(Vector3fc position, float radius) {
        for (int i = 0; i < 6; i++) {
            if (planes[i].getA() * position.x() + planes[i].getB() * position.y() + planes[i].getC() * position.z() + planes[i].getD() <= -radius) {
                return false;
            }
        }
        return true;
    }
}
