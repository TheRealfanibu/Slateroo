package ai.A3C;

import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.rl4j.network.ac.ActorCriticFactoryCompGraphStdDense;
import org.deeplearning4j.rl4j.network.ac.ActorCriticLoss;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.MultiDataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class NeuralNetwork {
    private ComputationGraph network;

    public NeuralNetwork() {
        a3cBuild();
    }

    private void a3cBuild() {
        int layer1Neurons = 16;

        ComputationGraphConfiguration config = new NeuralNetConfiguration.Builder()
                .seed(AIConstants.RANDOM_SEED)
                .updater(new RmsProp(AIConstants.LEARNING_RATE, AIConstants.RMS_PROP_DECAY,
                        RmsProp.DEFAULT_RMSPROP_EPSILON))
                .graphBuilder()
                .addInputs("input")
                .addLayer("Layer1", new DenseLayer.Builder()
                                .activation(Activation.RELU)
                                .nIn(AIConstants.NUM_STATES)
                                .nOut(layer1Neurons) //bit less?
                                .build(), "Layer1")
                .addLayer("Value", new OutputLayer.Builder()
                        .lossFunction(new ActorCriticLoss())
                        .activation(Activation.IDENTITY)
                        .nIn(layer1Neurons)
                        .nOut(1).build(), "Layer1")
                .addLayer("Policy", new OutputLayer.Builder()
                                .lossFunction(LossFunctions.LossFunction.MSE)
                                .activation(Activation.SOFTMAX)
                                .nIn(layer1Neurons)
                                .nOut(AIConstants.NUM_ACTIONS).build(), "Layer1")

                .setOutputs("Value", "Policy")
                .build();

        network = new ComputationGraph(config);
        network.init();
        network.setListeners(new ScoreIterationListener(10));
    }

    public INDArray[] predict(double[] states) {
        network.out
    }

    public void fit() {

    }

    /*private void mnistBuild() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Nesterovs())
        return network.output(Nd4j.create(states));
    }

    public void fit(double[] states) {
        network.
    }

    public Evaluation evaluate(DataSetIterator testData) {
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
        network.setListeners(new ScoreIterationListener(100), new PerformanceListener(100));
    }*/
}


