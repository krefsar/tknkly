import Ember from 'ember';
import RTM from 'npm:satori-sdk-js';

var channel = 'tknkly-channel';
var endpoint = "wss://open-data.api.satori.com";
var appKey = "CcBd9bF27E2C9fCCD74aab6fDDf9EabE";

export default Ember.Service.extend({
  currentRoll: 0,
  currentPitch: -90,
  centerYaw: null,
  currentYaw: 0,
  satoriRtm: null,

  initializeSatori() {
    var rtm = new RTM(endpoint, appKey);
    let subscription = rtm.subscribe(channel, RTM.SubscriptionMode.SIMPLE);

    subscription.on('rtm/subscription/data', (pdu) => {
        pdu.body.messages.forEach((msg) => {
          this.set('currentPitch', +msg.pitch);
          if (this.get('currentYaw')) {
            this.set('currentYaw', +msg.yaw);
          } else {
            this.set('centerYaw', +msg.yaw);
            this.set('currentYaw', +msg.yaw);
          }
          this.set('currentRoll', +msg.roll);
        });
    });

    this.set('satoriRtm', rtm);
  },

  startSatori() {
    let rtm = this.get('satoriRtm');
    rtm.start();
  }
});
