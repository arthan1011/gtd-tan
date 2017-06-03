import { test } from 'qunit';
import moduleForAcceptance from 'gtd/tests/helpers/module-for-acceptance';

moduleForAcceptance('Acceptance | main');

test('visiting /main and redirected to current sub route', function(assert) {
  visit('/main');

  andThen(function() {
    assert.equal(currentURL(), '/main/current');
  });
});

test('"Current" and "Archive" routes should be accessible', function (assert) {
  visit('/main');

  click('div.t-link_to_archive a').then(() => {
    assert.equal(currentURL(), '/main/archive');
  });

  click('div.t-link_to_current a').then(() => {
    assert.equal(currentURL(), '/main/current');
  });

});
