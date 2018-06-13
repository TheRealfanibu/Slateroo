package ai;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.PerformanceListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Adam;

import javax.xml.crypto.Data;

public class NeuralNetwork {
    public MultiLayerNetwork network;

    public NeuralNetwork() {
        build();
    }

    private void build() {
        long seed = 143256467;

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam())
                .l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(784)
                        .nOut(200)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new OutputLayer.Builder()
                        .nIn(200)
                        .nOut(10)
                        .activation(Activation.SOFTMAX)
                        .build())
                .backprop(true).pretrain(false).build();

        network = new MultiLayerNetwork(conf);
        network.setListeners(new ScoreIterationListener(10), new PerformanceListener(10));
    }

    public void train(DataSetIterator iterator) {
        network.fit(iterator);
    }
}
