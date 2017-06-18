import Ember from 'ember';
import config from './config/environment';

const Router = Ember.Router.extend({
  location: config.locationType,
  rootURL: config.rootURL
});

Router.map(function() {
  this.route('bench-press');
  this.route('calibration');
  this.route('bicep-curl');
});

export default Router;
