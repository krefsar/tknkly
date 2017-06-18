import Ember from 'ember';

export default Ember.Controller.extend({
  satoriManager: Ember.inject.service(),
  doneConnecting: false,

  init() {
    this._super();
    this.get('satoriManager').initializeSatori();
    let rtm = this.get('satoriManager').get('satoriRtm');
    rtm.on("enter-connected", () => {
        console.log("Connected to RTM!");
        this.send('connected');
    });
    console.log('starting rtm service');
    this.get('satoriManager').startSatori();
  },

  actions: {
    connected() {
      this.set('doneConnecting', true);
      Ember.run.later(this, () => {
        this.send('startBicepCurl');
      }, 2000);
    }
  }
});
