package ai;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

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
    }
}
