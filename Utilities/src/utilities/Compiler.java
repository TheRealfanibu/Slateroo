package utilities;

import java.io.File;
import java.lang.reflect.Method;

import javax.tools.*;

public class Compiler {
	private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	private StandardJavaFileManager fileManager;
	private Iterable<? extends JavaFileObject> compUnits;
	
	private File classFile;
	private File binClassFile;
	
	private Class<?> calledClass;
	private Method calledMethod;
	private Method invokerMethod;
	private Object[] methodArgs;
	
	public Object executeCode (File javaFile, String className, String methodName, Class<?>[] methodParamTypes, Object[] methodArgs,
			String invokerMethodName, Class<?>... invokerMethodParamTypes) {
		this.prepareExecution(javaFile, className, methodName, methodParamTypes, methodArgs, invokerMethodName, invokerMethodParamTypes);
		Object retrn = null;
		try {
			retrn = runCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		deleteFiles();
		
		return retrn;
	}
	
	public void prepareExecution(File javaFile, String className, String methodName, Class<?>[] methodParamTypes, Object[] methodArgs,
			String invokerMethodName, Class<?>... invokerMethodParamTypes) {
		compiler = ToolProvider.getSystemJavaCompiler();
		fileManager = compiler.getStandardFileManager(null, null, null);
		compUnits = fileManager.getJavaFileObjects(javaFile);
		compiler.getTask(null, fileManager, null, null, null, compUnits).call();
		String jFilePath = javaFile.getPath();
		String classFilePath = jFilePath.substring(0, jFilePath.lastIndexOf('.')) + ".class";
		copyClassFileToBin(classFilePath);
		try {
			calledClass = Class.forName(className);
			calledMethod = calledClass.getDeclaredMethod(methodName, methodParamTypes);
			invokerMethod = calledClass.getDeclaredMethod(invokerMethodName, invokerMethodParamTypes);
			if(methodArgs == null) {
				this.methodArgs = new Object[]{};
			}
			else {
				this.methodArgs = methodArgs;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void copyClassFileToBin (String classFilePath) {
		classFile = new File(classFilePath);
		String newPath = "bin/" + classFilePath;
		binClassFile = new File(newPath);
		FileUtils.copyFileToLocation(classFile, newPath);
	}
	
	public void deleteFiles() {
		classFile.delete();
		binClassFile.delete();
	}
	
	public Object runCode () throws Exception{
		return calledMethod.invoke(invokerMethod, methodArgs);
	}
}
