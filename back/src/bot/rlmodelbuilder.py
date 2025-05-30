import argparse
import os
from keras.models import Sequential
from keras.layers import Activation, Dense, Flatten, Conv2D, BatchNormalization, Input
from tensorflow.keras.optimizers import Adam

# from keras.optimizer_v2 import adam
from keras.layers import add as add_layer
from keras.models import Model
from tensorflow.python.keras.engine.keras_tensor import KerasTensor

# disable eager execution


from .config import *


class RLModelBuilder:
    """
    This class builds the neural network architecture according to the AlphaGo paper.
    """

    def __init__(self, input_shape: tuple, output_shape: tuple):
        """
        A neural network f that takes as input the raw board presentation s of the position and its history.
        It outputs move probabilities p and a value v:
        (p, v) = f(s)

        * p represents the probability of selecting each move.
        * v represents the probability of the current player winning the game in position s.
        """
        # disable_eager_execution()

        # define class variables
        self.input_shape = input_shape
        self.output_shape = output_shape
        self.nr_hidden_layers = AMOUNT_OF_RESIDUAL_BLOCKS
        self.convolution_filters = CONVOLUTION_FILTERS

        # tensorflow: gpu memory growth
        # gpus = tf.config.experimental.list_physical_devices('GPU')
        # [tf.config.experimental.set_memory_growth(
        #     gpu, True) for gpu in gpus if gpus]

    def build_model(self) -> Model:
        """
        Builds the neural network architecture
        """
        main_input = Input(shape=self.input_shape, name="main_input")

        x = self.build_convolutional_layer(main_input)

        # add a high amount of residual layers
        for _ in range(self.nr_hidden_layers):
            x = self.build_residual_layer(x)

        model = Model(inputs=main_input, outputs=x)

        policy_head = self.build_policy_head()
        value_head = self.build_value_head()

        model = Model(inputs=main_input, outputs=[policy_head(x), value_head(x)])

        model.compile(
            loss={
                "policy_head": "categorical_crossentropy",
                "value_head": "mean_squared_error",
            },
            optimizer=Adam(learning_rate=LEARNING_RATE),
            loss_weights={"policy_head": 0.5, "value_head": 0.5},
        )
        # return the compiled model
        return model

    def build_convolutional_layer(self, input_layer) -> KerasTensor:
        """
        Builds a convolutional layer
        """

        layer = Conv2D(
            filters=self.convolution_filters,
            kernel_size=(3, 3),
            strides=(1, 1),
            padding="same",
            data_format="channels_first",
            use_bias=False,
        )(input_layer)
        layer = BatchNormalization(axis=1)(layer)
        layer = Activation("relu")(layer)
        return layer

    def build_residual_layer(self, input_layer) -> KerasTensor:
        """
        Builds a residual layer
        """
        # first convolutional layer
        layer = self.build_convolutional_layer(input_layer)
        # second convolutional layer with skip connection
        layer = Conv2D(
            filters=self.convolution_filters,
            kernel_size=(3, 3),
            strides=(1, 1),
            padding="same",
            data_format="channels_first",
            use_bias=False,
        )(layer)
        layer = BatchNormalization(axis=1)(layer)
        # skip connection
        layer = add_layer([layer, input_layer])
        # activation function
        layer = Activation("relu")(layer)
        return layer

    def build_policy_head(self) -> Model:
        """
        Builds the policy head of the neural network
        """
        model = Sequential(name="policy_head")
        model.add(
            Conv2D(
                2,
                kernel_size=(1, 1),
                strides=(1, 1),
                input_shape=(
                    self.convolution_filters,
                    self.input_shape[1],
                    self.input_shape[2],
                ),
                padding="same",
                data_format="channels_first",
            )
        )
        model.add(BatchNormalization(axis=1))
        model.add(Activation("relu"))
        model.add(Flatten())
        model.add(Dense(self.output_shape[0], activation="sigmoid", name="policy_head"))
        return model

    def build_value_head(self) -> Model:
        """
        Builds the value head of the neural network
        """
        model = Sequential(name="value_head")
        model.add(
            Conv2D(
                1,
                kernel_size=(1, 1),
                strides=(1, 1),
                input_shape=(
                    self.convolution_filters,
                    self.input_shape[1],
                    self.input_shape[2],
                ),
                padding="same",
                data_format="channels_first",
            )
        )
        model.add(BatchNormalization(axis=1))
        model.add(Activation("relu"))
        model.add(Flatten())
        model.add(Dense(256))
        model.add(Activation("relu"))
        # output shape == 1, because we want 1 value: the estimated outcome from the position
        # tanh activation function maps the output to [-1, 1]
        model.add(Dense(self.output_shape[1], activation="tanh", name="value_head"))
        return model


if __name__ == "__main__":
    # parse arguments
    parser = argparse.ArgumentParser(description="Create the neural network for chess")
    parser.add_argument(
        "--model-folder", type=str, default="models/", help="Folder to save the model"
    )
    parser.add_argument(
        "--model-name",
        type=str,
        default="model",
        help="Name of the model (without extension)",
    )
    args = parser.parse_args()
    args = vars(args)

    # create the model
    model_builder = RLModelBuilder(
        input_shape=INPUT_SHAPE, output_shape=OUTPUT_SHAPE
    )
    model = model_builder.build_model()

    # create folders if they don't exist
    if not os.path.exists(args["model_folder"]):
        os.makedirs(args["model_folder"])

    # save the model
    print(f"Saving model to {args['model_folder']} as {args['model_name']}.keras ...")
    model.save(os.path.join(args["model_folder"], args["model_name"]) + ".keras")
