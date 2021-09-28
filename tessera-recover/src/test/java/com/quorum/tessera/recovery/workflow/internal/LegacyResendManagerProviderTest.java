package com.quorum.tessera.recovery.workflow.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import com.quorum.tessera.data.EncryptedTransactionDAO;
import com.quorum.tessera.discovery.Discovery;
import com.quorum.tessera.enclave.Enclave;
import com.quorum.tessera.enclave.EncodedPayloadCodec;
import com.quorum.tessera.enclave.PayloadEncoder;
import com.quorum.tessera.recovery.workflow.LegacyResendManager;
import com.quorum.tessera.transaction.publish.PayloadPublisher;
import java.util.Optional;
import org.junit.Test;

public class LegacyResendManagerProviderTest {

  @Test
  public void provider() {

    try (var enclaveMockedStatic = mockStatic(Enclave.class);
        var encryptedTransactionDAOMockedStatic = mockStatic(EncryptedTransactionDAO.class);
        var payloadEncoderMockedStatic = mockStatic(PayloadEncoder.class);
        var payloadPublisherMockedStatic = mockStatic(PayloadPublisher.class);
        var discoveryMockedStatic = mockStatic(Discovery.class)) {
      enclaveMockedStatic.when(Enclave::create).thenReturn(mock(Enclave.class));
      encryptedTransactionDAOMockedStatic
          .when(EncryptedTransactionDAO::create)
          .thenReturn(mock(EncryptedTransactionDAO.class));
      payloadEncoderMockedStatic
          .when(() -> PayloadEncoder.create(any(EncodedPayloadCodec.class)))
          .thenReturn(Optional.of(mock(PayloadEncoder.class)));
      payloadPublisherMockedStatic
          .when(PayloadPublisher::create)
          .thenReturn(mock(PayloadPublisher.class));
      discoveryMockedStatic.when(Discovery::create).thenReturn(mock(Discovery.class));

      LegacyResendManager legacyResendManager = LegacyResendManagerProvider.provider();

      assertThat(legacyResendManager).isNotNull();

      enclaveMockedStatic.verify(Enclave::create);
      enclaveMockedStatic.verifyNoMoreInteractions();

      encryptedTransactionDAOMockedStatic.verify(EncryptedTransactionDAO::create);
      encryptedTransactionDAOMockedStatic.verifyNoMoreInteractions();

      payloadEncoderMockedStatic.verify(
          () -> PayloadEncoder.create(any(EncodedPayloadCodec.class)));
      payloadEncoderMockedStatic.verifyNoMoreInteractions();

      payloadPublisherMockedStatic.verify(PayloadPublisher::create);
      payloadEncoderMockedStatic.verifyNoMoreInteractions();

      discoveryMockedStatic.verify(Discovery::create);
      discoveryMockedStatic.verifyNoMoreInteractions();
    }
  }

  @Test
  public void defaultConstructorForCoverage() {
    assertThat(new LegacyResendManagerProvider()).isNotNull();
  }
}
