package net.consensys.pantheon.ethereum.jsonrpc.internal.methods;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import net.consensys.pantheon.ethereum.core.SyncStatus;
import net.consensys.pantheon.ethereum.core.Synchronizer;
import net.consensys.pantheon.ethereum.jsonrpc.internal.JsonRpcRequest;
import net.consensys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcResponse;
import net.consensys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcSuccessResponse;
import net.consensys.pantheon.ethereum.jsonrpc.internal.results.SyncingResult;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EthSyncingTest {

  @Mock private Synchronizer synchronizer;
  private EthSyncing method;
  private final String JSON_RPC_VERSION = "2.0";
  private final String ETH_METHOD = "eth_syncing";

  @Before
  public void setUp() {
    method = new EthSyncing(synchronizer);
  }

  @Test
  public void returnsCorrectMethodName() {
    assertThat(method.getName()).isEqualTo(ETH_METHOD);
  }

  @Test
  public void shouldReturnFalseWhenSyncStatusIsEmpty() {
    final JsonRpcRequest request = requestWithParams();
    final JsonRpcResponse expectedResponse = new JsonRpcSuccessResponse(request.getId(), false);
    final Optional<SyncStatus> optionalSyncStatus = Optional.empty();
    when(synchronizer.getSyncStatus()).thenReturn(optionalSyncStatus);

    final JsonRpcResponse actualResponse = method.response(request);
    assertThat(actualResponse).isEqualToComparingFieldByField(expectedResponse);
    verify(synchronizer).getSyncStatus();
    verifyNoMoreInteractions(synchronizer);
  }

  @Test
  public void shouldReturnExpectedValueWhenSyncStatusIsNotEmpty() {
    final JsonRpcRequest request = requestWithParams();
    final SyncStatus expectedSyncStatus = new SyncStatus(0, 1, 2);
    final JsonRpcResponse expectedResponse =
        new JsonRpcSuccessResponse(request.getId(), new SyncingResult(expectedSyncStatus));
    final Optional<SyncStatus> optionalSyncStatus = Optional.of(expectedSyncStatus);
    when(synchronizer.getSyncStatus()).thenReturn(optionalSyncStatus);

    final JsonRpcResponse actualResponse = method.response(request);
    assertThat(actualResponse).isEqualToComparingFieldByField(expectedResponse);
    verify(synchronizer).getSyncStatus();
    verifyNoMoreInteractions(synchronizer);
  }

  private JsonRpcRequest requestWithParams(final Object... params) {
    return new JsonRpcRequest(JSON_RPC_VERSION, ETH_METHOD, params);
  }
}