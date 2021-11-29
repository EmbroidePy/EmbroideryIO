
package org.embroideryio.embroideryio;

//the below starts with two slashes for oracle java. 1 for android.
//*//
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

public class EmbMatrix extends AffineTransform {

    public void mapPoints(float[] p) {
        transform(p, 0, p, 0, 1);
    }

    public void postTranslate(float tx, float ty) {
        this.translate(tx, ty);
    }

    public void postScale(float sx, float sy) {
        this.scale(sx, sy);
    }

    public void postRotate(float theta) {
        this.rotate(theta);
    }

    public void invert(EmbMatrix matrix) {
        try {
            this.invert();
        } catch (NoninvertibleTransformException ex) {
        }
    }

    public void postScale(float sx, float sy, float x, float y) {
        this.translate(x, y);
        this.scale(sx, sy);
        this.translate(-x, -y);
    }

    public void postRotate(float theta, float x, float y) {
        this.translate(x, y);
        this.rotate(theta);
        this.translate(-x, -y);
    }

}
/*/

import android.graphics.Matrix;

public class EmbMatrix extends Matrix {
    
}
//*/