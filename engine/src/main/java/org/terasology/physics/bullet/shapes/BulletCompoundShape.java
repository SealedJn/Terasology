/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.physics.bullet.shapes;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.terasology.physics.shapes.CollisionShape;
import org.terasology.physics.shapes.CompoundShape;

import java.util.ArrayList;
import java.util.List;

public class BulletCompoundShape extends BulletCollisionShape implements CompoundShape {
    private final btCompoundShape compoundShape;
    private List<BulletCompoundShapeChild> childList;

    public BulletCompoundShape() {
        this(new btCompoundShape());
    }

    private BulletCompoundShape(btCompoundShape compoundShape) {
        this.compoundShape = compoundShape;
        underlyingShape = compoundShape;

        childList = new ArrayList<>();
    }

    @Override
    public void addChildShape(Vector3fc origin, Quaternionfc rotation, float scale, CollisionShape collisionShape) {
        BulletCollisionShape bulletCollisionShape = (BulletCollisionShape) collisionShape;

        compoundShape.addChildShape(new Matrix4f().translationRotateScale(origin, rotation, scale), ((BulletCollisionShape) collisionShape).underlyingShape);
        childList.add(new BulletCompoundShapeChild(origin, rotation, scale, bulletCollisionShape, compoundShape.getChildShape(compoundShape.getNumChildShapes() - 1)));
    }

    // TODO: Add removeChildShape if needed

    @Override
    public CollisionShape rotate(Quaternionf rot) {
        BulletCompoundShape shape = new BulletCompoundShape();
        for (BulletCompoundShapeChild child : childList) {
            Matrix4f transform = new Matrix4f().rotate(rot).mul(child.transform);
            shape.compoundShape.addChildShape(transform, child.childShape.underlyingShape);
            shape.childList.add(new BulletCompoundShapeChild(transform, child.childShape, compoundShape.getChildShape(compoundShape.getNumChildShapes() - 1)));
        }
        return shape;
    }

    private static class BulletCompoundShapeChild {
        public BulletCollisionShape childShape;

        public btCollisionShape compoundShapeChild;
        private final Matrix4f transform = new Matrix4f();

        private BulletCompoundShapeChild(Matrix4fc trans, BulletCollisionShape childShape, btCollisionShape compoundShapeChild) {
            this.transform.set(trans);
            this.childShape = childShape;
            this.compoundShapeChild = compoundShapeChild;
        }

        private BulletCompoundShapeChild(Vector3fc origin, Quaternionfc rotation, float scale, BulletCollisionShape childShape, btCollisionShape compoundShapeChild) {
            this.transform.translationRotateScale(origin, rotation, scale);
            this.childShape = childShape;
            this.compoundShapeChild = compoundShapeChild;
        }
    }
}
