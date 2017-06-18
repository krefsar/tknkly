import Ember from 'ember';
import RTM from 'npm:satori-sdk-js';

var channel = 'tknkly-channel';
var endpoint = "wss://open-data.api.satori.com";
var appKey = "CcBd9bF27E2C9fCCD74aab6fDDf9EabE";

export default Ember.Service.extend({
  currentPitch: -90,
  currentYaw: 0,
  satoriRtm: null,

  initializeSatori() {
    console.log('initializing satori');
    var rtm = new RTM(endpoint, appKey);
    let subscription = rtm.subscribe(channel, RTM.SubscriptionMode.SIMPLE);
    
    subscription.on('rtm/subscription/data', (pdu) => {
        pdu.body.messages.forEach((msg) => {
          console.log(`yaw: ${msg.pitch}`);
          this.set('currentPitch', +msg.pitch);
          this.set('currentYaw', +msg.yaw);
        });
    });

    this.set('satoriRtm', rtm);
  },

  startSatori() {
    let rtm = this.get('satoriRtm');
    rtm.start();
  }
});
