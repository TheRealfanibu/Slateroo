package ai;

import com.sun.deploy.util.ArrayUtil;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import org.apache.commons.lang3.ArrayUtils;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class ND4JTest {
    public static void main(String[] args) {
        double[][] a = {
                {1,1,2},
                {1,1,1}
        };
        double[][] b = {
                {2,2,2},
                {2,2,2}
        };
        INDArray x = Nd4j.create(a);
        INDArray y = Nd4j.create(b);

        double[] c = {1d,1d,1d};
        System.out.println();
    }
}
