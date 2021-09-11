package org.iotcity.iot.framework.net.support.actor;

/**
 * The actor method class factory.
 * @author ardon
 * @date 2021-09-12
 */
public interface NetActorClassFactory {

	/**
	 * Gets the method parameter types of command.
	 * @param command The actor command information.
	 * @return The parameter types for the method.
	 */
	Class<?>[] getParameterTypes(NetActorCommand command);

	/**
	 * Gets the method return type of command.
	 * @param command The actor command information.
	 * @return The return type for the method.
	 */
	Class<?> getReturnType(NetActorCommand command);

}
