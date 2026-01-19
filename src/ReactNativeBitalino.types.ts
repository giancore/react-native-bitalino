export type BitalinoDeviceEvent = {
  device: {
    name: string;
    address: string;
  };
};

export type BitalinoFrameEvent = {
  frame: {
    identifier: string;
    seq: number;
    analog: number[];
    digital: number[];
  };
};

export type BitalinoStateEvent = {
  stateId: number;
  state: string;
};

export type BitalinoCommandReplyEvent = {
  type: "state" | "description";
  data?: {
    identifier: string;
    analog: number[];
    battery: number;
    batThreshold: number;
    digital: number[];
  };
  isBITalino2?: boolean;
  firmwareVersion?: number;
};

export type BitalinoConnectionState =
  | "DISCONNECTED"
  | "CONNECTING"
  | "CONNECTED"
  | "ACQUISITION_STARTED"
  | "ACQUISITION_STOPPED"
  | "ERROR";

export type BitalinoDeviceInfo = {
  address: string;
  state: BitalinoConnectionState;
  isConnected: boolean;
  sampleRate: number;
  activeChannels: number[];
  isAcquiring: boolean;
};

export type BitalinoGetBatteryResponse = {
  requested: boolean;
  message: string;
};
