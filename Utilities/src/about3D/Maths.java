package about3D;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Maths {
	public static Vector3f mul(Vector3f vec, float scale) {
		return new Vector3f(vec.x * scale, vec.y * scale, vec.z * scale);
	}
	
	public static Vector2f mul(Vector2f vec, float scale) {
		return new Vector2f(vec.x * scale, vec.y * scale);
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f trans, Vector3f rot, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(trans, matrix, matrix);
		Matrix4f.rotate((float)Math.toRadians(rot.x), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float)Math.toRadians(rot.y), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float)Math.toRadians(rot.z), new Vector3f(0,0,1), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createProjectionMatrix(float fov, float nearPlane, float farPlane) {
		 float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
	        float y_scale = (float) ((1f / Math.tan(Math.toRadians(fov / 2f))) * aspectRatio);
	        float x_scale = y_scale / aspectRatio;
	        float frustum_length = farPlane - nearPlane;
	 
	        Matrix4f projectionMatrix;
	        projectionMatrix = new Matrix4f();
	        projectionMatrix.m00 = x_scale;
	        projectionMatrix.m11 = y_scale;
	        projectionMatrix.m22 = -((farPlane + nearPlane) / frustum_length);
	        projectionMatrix.m23 = -1;
	        projectionMatrix.m32 = -((2 * nearPlane * farPlane) / frustum_length);
	        projectionMatrix.m33 = 0;
	        return projectionMatrix;
	}
	
	 public static Matrix4f createViewMatrix(Cam cam) {
	        Matrix4f viewMatrix = new Matrix4f();
	        viewMatrix.setIdentity();
	        Matrix4f.rotate((float) Math.toRadians(cam.getPitch()), new Vector3f(1, 0, 0), viewMatrix,
	                viewMatrix);
	        Matrix4f.rotate((float) Math.toRadians(cam.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
	        Vector3f cameraPos = cam.getPosition();
	        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
	        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
	        return viewMatrix;
	}
}
