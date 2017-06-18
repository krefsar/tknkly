import Ember from 'ember';
import RTM from 'npm:satori-sdk-js';

var channel = 'tknkly-message';
var endpoint = "wss://open-data.api.satori.com";
var appKey = 'ebb3A5988CEA09fc1FAA4a2CA8cE0740';
var role = "tknkly-message";
var roleSecretKey = "2F3f4f471E5c1ae16718CAfacE1cEbF7";
var roleSecretProvider = RTM.roleSecretAuthProvider(role, roleSecretKey);


export default Ember.Service.extend({
  rollError: false,
  yawError: false,
  pitchError: false,
  satoriRtm: null,

  initializeSatori() {
    var rtm = new RTM(endpoint, appKey, {
      authProvider: roleSecretProvider
    });

    let subscription = rtm.subscribe(channel, RTM.SubscriptionMode.SIMPLE);

    subscription.on('rtm/subscription/data', (pdu) => {
        pdu.body.messages.forEach((msg) => {
          if (msg.type === 'roll') {
            this.set('rollError', true);
            Ember.run.debounce(this, () => {
              this.set('rollError', false);
            }, 1000);
          } else if (msg.type === 'pitch') {
            this.set('pitchError', true);
            Ember.run.debounce(this, () => {
              this.set('pitchError', false);
            }, 1000);
          } else if (msg.type === 'yaw') {
            this.set('yawError', true);
            Ember.run.debounce(this, () => {
              this.set('yawError', false);
            }, 1000);
          }
        });
    });

    this.set('satoriRtm', rtm);
  },

  clearErrors() {
    this.set('hasError', false);
  },

  startSatori() {
    let rtm = this.get('satoriRtm');
    rtm.start();
  }
});
