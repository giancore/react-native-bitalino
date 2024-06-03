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
