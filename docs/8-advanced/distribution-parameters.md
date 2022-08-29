#  Input Parameters for Distributions

## InputParameterMapDistContinuous and InputParameterDistContinuousSelection

The user-based (interactive) entry of distribution functions makes heavy use of the `InputParameterSelectionMap` and the `AbstractInputParameterTypedMap`. This is because it is impossible for the user to enter a distribution function and its parameters in a user interface (unless we ask to enter the distribution function as a string and then parse the string). In a 'friendly' environment, the user will have to select a **type** of distribution function first, followed by the specification of one or more **parameters** for that distribution function. The `InputParameterMapDistContinuous` and `InputParameterDistContinuousSelection` classes have been created for that purpose:

![](input-parameters-dist.png)

The `InputParameterMapDistContinuous` is an `InputParameterMap` analogous to the `InputParameterDistContinuous` as it contains a random stream, and the distribution can be retrieved. The `InputParameterDistContinuousSelection` class allows the user to choose one of the `InputParameterDistContinuous` maps and specify the corresponding parameters for the distribution. The `InputParameterDistContinuousSelection` is therefore defined as follows:

```java
public class InputParameterDistContinuousSelection 
    extends InputParameterSelectionMap<String, InputParameterMapDistContinuous>
```

Internally, it contains a map of options, one for each of the distribution functions:

```java
private static SortedMap<String, InputParameterMapDistContinuous> distOptions;
...
distOptions = new TreeMap<>();
distOptions.put("Beta", new Beta());
distOptions.put("Constant", new Constant());
distOptions.put("Erlang", new Erlang());
distOptions.put("Exponential", new Exponential());
distOptions.put("Gamma", new Gamma());
distOptions.put("LogNormal", new LogNormal());
distOptions.put("Normal", new Normal());
distOptions.put("Pearson5", new Pearson5());
distOptions.put("Pearson6", new Pearson6());
distOptions.put("Triangular", new Triangular());
distOptions.put("Uniform", new Uniform());
distOptions.put("Weibull", new Weibull());
```

Internally, the `Normal` distribution has, e.g. the following definition:

```java
public Normal() throws InputParameterException
{
    super("Normal", "Normal", "Normal distribution", 1.0);
    add(new InputParameterDouble("mu", 
        "mu", "mu value, mean of the Normal distribution", 
        0.0, -Double.MAX_VALUE, Double.MAX_VALUE, false, false, "%f", 1.0));
    add(new InputParameterDouble("sigma", "sigma", 
        "sigma value, standard deviation of the Normal distribution", 1.0,
        0.0, Double.MAX_VALUE, true, false, "%f", 2.0));
}
```

So the user can first define the type of distribution, and then --one level deeper in the tree-- specify the parameters for that distribution. Each of the distributions mentioned above has a subclass of `InputParameterMapDistContinuous` that allows the right parameters to be specified.

The code for the Discrete distributions works analogously.
