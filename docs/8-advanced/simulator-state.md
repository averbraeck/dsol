# Simulator State

The `Simulator` in DSOL is a multi-threaded state machine. It contains a `RunState` that indicates the state that the model execution is in. This is important, since the multi-threaded character of the application means that changes are not instantaneous. As an example, when the simulator is told to stop with a `stop()` method call, and the current event being executed takes long to complete, the `Simulator` won't stop until the event execution has completed. We have a special state for that indicating that the Simulator is `STOPPING`. After the event completion, the `Simulator` is `STOPPED`. Depending on the state of the `Simulator` certain actions are or are not allowed. 

Similarly, the execution of a `Replication` can be in different states. For instance, a replication can be `STARTING`, meaning that the model is being constructed and the data structures for the model are being set-up, but this is different from `STARTED` where model code has actually executed. Again, the state machine for the `ReplicationState` allows certain actions to take place, and forbids other  actions. The `RunState` and the `ReplicationState` are obviously related to each other.


## RunState

RunState has the following possible values: `NOT_INITIALIZED`, `INITIALIZED`, `STARTING`, `STARTED`, `STOPPING`, `STOPPED`, `ENDED`.

The `RunState` is the communication between the main thread or user interface (UI) thread and the `SimulatorWorkerThread`. Initial states `NOT_INITIALIZED`, and `INITIALIZED` shall be set only by the main / UI thread. `NOT_INITIALIZED` is set in the constructor and the cleanup() method; `INITIALIZED` Is set by the `initialize(...)` method, and indicates that the simulator can be started.

`STARTING` and `STOPPING` shall be set only by the main thread / UI thread, and indicate the intent to start or stop the simulator. Actual starting or stopping can take a while after the RunState has been set, because the simulation itself is executed by another thread: the `SimulatorWorkerThread`.

`STARTING`: After starting has been set, an interrupt is sent to the `SimulatorWorkerThread` to wake it up from its `Thread wait()` state. The `SimulatorWorkerThread.run()` method fires a `START_EVENT` to listeners (e.g., the GUI), and sets the RunState to `STARTED`, just before the Simulator's `run()` method is called. It can take some time to wake up -- so there is an unspecified time between the `RunState.STARTING` and `RunState.STARTED` states. As long as the `Simulator.run()` is not finished (either because of calling `stop()`, an error In the simulation, an empty event list, or reaching the replication's run length), it will stay in a loop executing events in the `SimulatorWorkerThread`.

`STOPPING`: Between the execution of events, the Simulator's `run()` method (executed by the `SimulatorWorkerThread`) checks the `RunState`. When it Is found to be `STOPPING`, the Simulator's run() method ends, and the thread continues the execution of the SimulatorWorkerThread's `run()` method. Here, it will first set the `RunState` to `STOPPED`. Then, it will check whether the replication has to end (see `ReplicationState`). If so, it will set the `ReplicationState` to `ENDED`, and the RunState to `ENDED` as well. It will also fire an `END_REPLICATION_EVENT`.

`STOPPED`: After a `Simulator` has been stopped, it can be started again -- unless the end of the replication has been reached. Stopping the `Simulator` actually pauses the `SimulatorWorkerThread`, rather than terminating it.

The `RunState` acts as a state machine that guards the correct sequence of states for the simulation run. When an action is attempted that does not fit the state machine's logic, such as starting an already started simulator, or starting or stopping a simulation that has completely ended, an error can be raised.

!!! Note
    Note that the `endReplication()` method is not needed. The Simulator's `run()` method should take care of flagging whether the replication has ended -- and act accordingly. The `warmup()` event, on the other hand, is needed. By making the `SimulatorWorkerThread` responsible for the handling of the end of the replication, this does not have to be separately implemented in different Simulator implementations. Any object interested in the end of the replication (e.g., to collect statistics), should listen to the `END_REPLICATION_EVENT`.


## ReplicationState

`ReplicationState` has the following possible values: `NOT_INITIALIZED`, `INITIALIZED`, `STARTING`, `STARTED`, `ENDING`, `ENDED`.

The `ReplicationState` is used to keep track of the events that have or have not been sent for the replication. As an example, when `Simulator.start()` is called, and the simulator time is zero, one does not know whether the `START_REPLICATION_EVENT` still needs to be fired. When the simulation has multiple events at t=0, and it has executed `step()`, followed by `start()`, `START_REPLICATION_EVENT` has already been fired by the `step()` method. Without the `ReplicationState`, the `Simulator` has no way of knowing that the replication has already been started before. The `ReplicationState` helps by providing a clear sequence of states for a normal simulation run.

Initial states `NOT_INITIALIZED`, and `INITIALIZED` shall be set only by the main / UI thread. `NOT_INITIALIZED` is set in the constructor and the `cleanup()` method; `INITIALIZED` Is set by the `initialize(...)` method, and indicates that the replication can be started.

The `ReplicationState.STARTED` state is set in the `startImpl()` or the `step()` method after checking that the `ReplicationState` is `INITIALIZED`. This is important, since also the `START_REPLICATION_EVENT` is fired in `startImpl()` or `step()` when `ReplicationState` is `INITIALIZED`, ensuring that the `START_REPLICATION_EVENT` is only fired once.

The `ENDING` state should be set by the Simulator's `run()` method based on the state of the simulation and the event list. When the event list is empty or the simulator has reached the replication's run time, the `ReplicationState` can be set to `ENDING`. When the `run()` is interrupted by a `stop()` or when an error occurred, the `ReplicationState` can be left in the `STARTED` state.

A setting of `ENDING` for the `ReplicationState` is (and should) always be mirrored by a `STOPPING` state for the `RunState`. When the `Simulator.run()` method ends with `RunState.STOPPING` and `ReplicationState.ENDING`, the `SimulatorWorkerThread` will take care of firing an `END_REPLICATION_EVENT` and setting both the `ReplicationState` and the `RunState` to `ENDED`.



