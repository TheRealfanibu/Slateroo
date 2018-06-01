package about3D;

import org.lwjgl.util.vector.Vector3f;

public interface Cam {
	Vector3f getPosition();
	float getPitch();
	float getYaw();
	float getRoll();
}
