package info.novatec.inspectit.cmr.instrumentation.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import info.novatec.inspectit.ci.AgentMapping;
import info.novatec.inspectit.ci.AgentMappings;
import info.novatec.inspectit.ci.Environment;
import info.novatec.inspectit.ci.Profile;
import info.novatec.inspectit.ci.assignment.impl.ExceptionSensorAssignment;
import info.novatec.inspectit.ci.assignment.impl.FunctionalMethodSensorAssignment;
import info.novatec.inspectit.ci.assignment.impl.MethodSensorAssignment;
import info.novatec.inspectit.ci.exclude.ExcludeRule;
import info.novatec.inspectit.ci.factory.FunctionalMethodSensorAssignmentFactory;
import info.novatec.inspectit.cmr.ci.ConfigurationInterfaceManager;
import info.novatec.inspectit.cmr.service.IRegistrationService;
import info.novatec.inspectit.exception.BusinessException;
import info.novatec.inspectit.instrumentation.config.applier.IInstrumentationApplier;
import info.novatec.inspectit.testbase.TestBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
public class ConfigurationResolverTest extends TestBase {

	protected final static String PROFILE_ID = "id";

	@InjectMocks
	protected ConfigurationResolver configurationResolver;

	@Mock
	protected ConfigurationInterfaceManager configurationInterfaceManager;

	@Mock
	protected IRegistrationService registrationService;

	@Mock
	protected FunctionalMethodSensorAssignmentFactory functionalAssignmentFactory;

	@Mock
	protected Environment environment;

	@Mock
	protected Profile profile;

	public static class EnvironmentForAgent extends ConfigurationResolverTest {

		@Mock
		private AgentMappings agentMappings;

		private final String agentName = "inspectit";

		private final List<String> definedIPs = Collections.singletonList("127.0.0.1");

		@BeforeMethod
		public void initMappings() {
			when(configurationInterfaceManager.getAgentMappings()).thenReturn(agentMappings);
		}

		@Test(expectedExceptions = { BusinessException.class })
		public void noMappings() throws BusinessException {
			when(agentMappings.getMappings()).thenReturn(Collections.<AgentMapping> emptyList());
			configurationResolver.getEnvironmentForAgent(definedIPs, agentName);
		}

		@Test(expectedExceptions = { BusinessException.class })
		public void noMatchingMappingsName() throws BusinessException {
			AgentMapping mapping = mock(AgentMapping.class);
			when(agentMappings.getMappings()).thenReturn(Collections.singletonList(mapping));

			when(mapping.getAgentName()).thenReturn("something else");
			when(mapping.isActive()).thenReturn(true);
			configurationResolver.getEnvironmentForAgent(definedIPs, agentName);
		}

		@Test(expectedExceptions = { BusinessException.class })
		public void noMatchingMappingsNameWildcard() throws BusinessException {
			AgentMapping mapping = mock(AgentMapping.class);
			when(agentMappings.getMappings()).thenReturn(Collections.singletonList(mapping));

			when(mapping.getAgentName()).thenReturn("ins*TT");
			when(mapping.isActive()).thenReturn(true);
			configurationResolver.getEnvironmentForAgent(definedIPs, agentName);
		}

		@Test(expectedExceptions = { BusinessException.class })
		public void noMatchingMappingsIp() throws BusinessException {
			AgentMapping mapping = mock(AgentMapping.class);
			when(agentMappings.getMappings()).thenReturn(Collections.singletonList(mapping));

			when(mapping.getAgentName()).thenReturn("*");
			when(mapping.getIpAddress()).thenReturn("128.0.0.1");
			when(mapping.isActive()).thenReturn(true);
			configurationResolver.getEnvironmentForAgent(definedIPs, agentName);
		}

		@Test(expectedExceptions = { BusinessException.class })
		public void noMatchingMappingsIpWildcard() throws BusinessException {
			AgentMapping mapping = mock(AgentMapping.class);
			when(agentMappings.getMappings()).thenReturn(Collections.singletonList(mapping));

			when(mapping.getAgentName()).thenReturn("*");
			when(mapping.getIpAddress()).thenReturn("127.*.2");
			when(mapping.isActive()).thenReturn(true);
			configurationResolver.getEnvironmentForAgent(definedIPs, agentName);
		}

		@Test(expectedExceptions = { BusinessException.class })
		public void twoMatchingMappings() throws BusinessException {
			AgentMapping mapping1 = mock(AgentMapping.class);
			AgentMapping mapping2 = mock(AgentMapping.class);
			List<AgentMapping> mappings = new ArrayList<>();
			mappings.add(mapping1);
			mappings.add(mapping2);
			when(agentMappings.getMappings()).thenReturn(mappings);

			when(mapping1.getAgentName()).thenReturn("*");
			when(mapping1.isActive()).thenReturn(true);
			when(mapping1.getIpAddress()).thenReturn("*");
			when(mapping2.getAgentName()).thenReturn("*");
			when(mapping2.getIpAddress()).thenReturn("*");
			when(mapping2.isActive()).thenReturn(true);
			configurationResolver.getEnvironmentForAgent(definedIPs, agentName);
		}

		@Test
		public void oneMatchingMapping() throws BusinessException {
			AgentMapping mapping1 = mock(AgentMapping.class);
			AgentMapping mapping2 = mock(AgentMapping.class);
			List<AgentMapping> mappings = new ArrayList<>();
			mappings.add(mapping1);
			mappings.add(mapping2);
			when(agentMappings.getMappings()).thenReturn(mappings);

			when(mapping1.getAgentName()).thenReturn("ins*");
			when(mapping1.isActive()).thenReturn(true);
			when(mapping1.getIpAddress()).thenReturn("*");
			when(mapping2.getAgentName()).thenReturn("something else");
			when(mapping2.isActive()).thenReturn(true);
			when(mapping1.getEnvironmentId()).thenReturn("env1");
			Environment environment = mock(Environment.class);
			when(configurationInterfaceManager.getEnvironment("env1")).thenReturn(environment);

			assertThat(configurationResolver.getEnvironmentForAgent(definedIPs, agentName), is(environment));
		}

		@Test(expectedExceptions = { BusinessException.class })
		public void inactiveMapping() throws BusinessException {
			AgentMapping mapping1 = mock(AgentMapping.class);
			when(agentMappings.getMappings()).thenReturn(Collections.singleton(mapping1));

			when(mapping1.getAgentName()).thenReturn("ins*");
			when(mapping1.isActive()).thenReturn(false);
			when(mapping1.getIpAddress()).thenReturn("*");
			when(mapping1.getEnvironmentId()).thenReturn("env1");
			Environment environment = mock(Environment.class);
			when(configurationInterfaceManager.getEnvironment("env1")).thenReturn(environment);

			configurationResolver.getEnvironmentForAgent(definedIPs, agentName);
		}
	}

	public static class InstrumentationAppliers extends ConfigurationResolverTest {

		@Mock
		private IInstrumentationApplier instrumentationApplier;

		@Test
		public void nullEnvironment() {
			Collection<IInstrumentationApplier> appliers = configurationResolver.getInstrumentationAppliers(null);

			assertThat(appliers, is(empty()));

			verifyZeroInteractions(functionalAssignmentFactory, configurationInterfaceManager);
		}

		@Test
		public void noProfile() {
			when(environment.getProfileIds()).thenReturn(Collections.<String> emptySet());

			Collection<IInstrumentationApplier> appliers = configurationResolver.getInstrumentationAppliers(environment);

			assertThat(appliers, is(empty()));

			verify(functionalAssignmentFactory).getFunctionalAssignments(environment);
		}

		@Test
		public void profileDoesNotExists() throws BusinessException {
			when(environment.getProfileIds()).thenReturn(Collections.singleton(PROFILE_ID));
			when(configurationInterfaceManager.getProfile(PROFILE_ID)).thenThrow(new BusinessException(null));

			Collection<IInstrumentationApplier> appliers = configurationResolver.getInstrumentationAppliers(environment);

			assertThat(appliers, is(empty()));

			verify(functionalAssignmentFactory).getFunctionalAssignments(environment);
		}

		@Test
		public void profileNotActive() throws BusinessException {
			MethodSensorAssignment assignment = mock(MethodSensorAssignment.class);
			when(assignment.getInstrumentationApplier(environment, registrationService)).thenReturn(instrumentationApplier);
			when(environment.getProfileIds()).thenReturn(Collections.singleton(PROFILE_ID));
			when(configurationInterfaceManager.getProfile(PROFILE_ID)).thenReturn(profile);
			when(profile.getMethodSensorAssignments()).thenReturn(Collections.singletonList(assignment));
			when(profile.isActive()).thenReturn(false);

			Collection<IInstrumentationApplier> appliers = configurationResolver.getInstrumentationAppliers(environment);

			assertThat(appliers, is(empty()));

			verify(functionalAssignmentFactory).getFunctionalAssignments(environment);
			verifyZeroInteractions(assignment);
		}

		@Test
		public void methodSensorAssignment() throws BusinessException {
			MethodSensorAssignment assignment = mock(MethodSensorAssignment.class);
			when(assignment.getInstrumentationApplier(environment, registrationService)).thenReturn(instrumentationApplier);
			when(environment.getProfileIds()).thenReturn(Collections.singleton(PROFILE_ID));
			when(configurationInterfaceManager.getProfile(PROFILE_ID)).thenReturn(profile);
			when(profile.getMethodSensorAssignments()).thenReturn(Collections.singletonList(assignment));
			when(profile.isActive()).thenReturn(true);

			Collection<IInstrumentationApplier> appliers = configurationResolver.getInstrumentationAppliers(environment);

			assertThat(appliers, hasSize(1));
			assertThat(appliers, hasItem(instrumentationApplier));

			verify(assignment).getInstrumentationApplier(environment, registrationService);
			verify(functionalAssignmentFactory).getFunctionalAssignments(environment);
		}

		@Test
		public void exceptionSensorAssignment() throws BusinessException {
			ExceptionSensorAssignment assignment = mock(ExceptionSensorAssignment.class);
			when(assignment.getInstrumentationApplier(environment, registrationService)).thenReturn(instrumentationApplier);
			when(environment.getProfileIds()).thenReturn(Collections.singleton(PROFILE_ID));
			when(configurationInterfaceManager.getProfile(PROFILE_ID)).thenReturn(profile);
			when(profile.getExceptionSensorAssignments()).thenReturn(Collections.singletonList(assignment));
			when(profile.isActive()).thenReturn(true);

			Collection<IInstrumentationApplier> appliers = configurationResolver.getInstrumentationAppliers(environment);

			assertThat(appliers, hasSize(1));
			assertThat(appliers, hasItem(instrumentationApplier));

			verify(assignment).getInstrumentationApplier(environment, registrationService);
			verify(functionalAssignmentFactory).getFunctionalAssignments(environment);
		}

		@Test
		public void functionalAssignments() {
			FunctionalMethodSensorAssignment assignment = mock(FunctionalMethodSensorAssignment.class);
			when(assignment.getInstrumentationApplier(environment, registrationService)).thenReturn(instrumentationApplier);
			when(functionalAssignmentFactory.getFunctionalAssignments(environment)).thenReturn(Collections.singletonList(assignment));

			Collection<IInstrumentationApplier> appliers = configurationResolver.getInstrumentationAppliers(environment);

			assertThat(appliers, hasSize(1));
			assertThat(appliers, hasItem(instrumentationApplier));

			verify(assignment).getInstrumentationApplier(environment, registrationService);
			verify(functionalAssignmentFactory).getFunctionalAssignments(environment);
		}

	}

	public static class ExcludeRules extends ConfigurationResolverTest {

		@Mock
		private ExcludeRule excludeRule;

		@Test
		public void nullEnvironment() {
			Collection<ExcludeRule> rules = configurationResolver.getAllExcludeRules(null);

			assertThat(rules, is(empty()));

			verifyZeroInteractions(configurationInterfaceManager);
		}

		@Test
		public void noProfile() {
			when(environment.getProfileIds()).thenReturn(Collections.<String> emptySet());

			Collection<ExcludeRule> rules = configurationResolver.getAllExcludeRules(environment);

			assertThat(rules, is(empty()));
		}

		@Test
		public void profileDoesNotExists() throws BusinessException {
			when(environment.getProfileIds()).thenReturn(Collections.singleton(PROFILE_ID));
			when(configurationInterfaceManager.getProfile(PROFILE_ID)).thenThrow(new BusinessException(null));

			Collection<ExcludeRule> rules = configurationResolver.getAllExcludeRules(environment);

			assertThat(rules, is(empty()));
		}

		@Test
		public void profileNotActive() throws BusinessException {
			when(environment.getProfileIds()).thenReturn(Collections.singleton(PROFILE_ID));
			when(configurationInterfaceManager.getProfile(PROFILE_ID)).thenReturn(profile);
			when(profile.getExcludeRules()).thenReturn(Collections.singletonList(excludeRule));
			when(profile.isActive()).thenReturn(false);

			Collection<ExcludeRule> rules = configurationResolver.getAllExcludeRules(environment);

			assertThat(rules, is(empty()));
		}

		@Test
		public void excludeRule() throws BusinessException {
			when(environment.getProfileIds()).thenReturn(Collections.singleton(PROFILE_ID));
			when(configurationInterfaceManager.getProfile(PROFILE_ID)).thenReturn(profile);
			when(profile.getExcludeRules()).thenReturn(Collections.singletonList(excludeRule));
			when(profile.isActive()).thenReturn(true);

			Collection<ExcludeRule> rules = configurationResolver.getAllExcludeRules(environment);

			assertThat(rules, hasSize(1));
			assertThat(rules, hasItem(excludeRule));
		}

	}

}